(ns nes.memory
  (:use [nes.opcodes]))

(defn new-memory
  "Creates a new 64kb memory space."
  []
  (->> (repeat 65536 0x00)
       (vec)))

(defn combine-bytes
  [msb lsb]
  (bit-or
    (bit-shift-left msb 8)
    lsb))

(defn write16 [system address value]
  (let [msb (-> value
                (bit-shift-right 8)
                (bit-and 0xFF))
        lsb (bit-and value 0xFF)]
    (-> system
        (assoc-in [:mem address] lsb)
        (assoc-in [:mem (bit-and 0xFFFF (inc address))] msb))))

(defn write8 [system address value]
  (assoc-in system [:mem address] (bit-and 0xFF value)))

(defn read-operand
  "Gets an operand form a given address given a
  size (0, 1, or 2 bytes)"
  [mem addr size]
  (case size
    0 nil
    1 (get mem addr)
    2 (combine-bytes
        (get mem (inc addr))
        (get mem addr))))

(defn get-current-instruction
  [system]
  (let [opcode (get (:mem system) (:pc system))
        instruction (get instruction-set opcode)
        operand-size ((:address-mode instruction) operand-sizes)
        operand (read-operand
                 (:mem system)
                 (inc (:pc system))
                 operand-size)]
    (assoc instruction :operand operand)))

(defn indirect-address
  [mem operand]
  (let [msb (get mem (bit-and (inc operand) 0xFFFF))
        lsb (get mem (bit-and operand 0xFFFF))]
    (combine-bytes msb lsb)))

(defn indirect-y
  [system operand]
  (-> (:mem system)
      (indirect-address operand)
      (+ (:y system))
      (bit-and 0xFFFF)))

(defn indirect-x
  [system operand]
  (let [x-offset (bit-and (+ operand (:x system)) 0xFF)]
    (-> (:mem system)
        (indirect-address x-offset))))

(defn zeropage-reg8
  [operand reg]
  (bit-and (+ reg operand)
           0xFF))

(defn absolute-reg16
  [operand reg]
  (bit-and (+ reg operand)
           0xFFFF))

(defn same-page?
  "Returns whether two addresses are within the same page."
  [addr1 addr2]
  (= (bit-and 0xFF00 addr1)
     (bit-and 0xFF00 addr2)))

(defn resolve-address
  [system instruction]
  (let [mem (:mem system)
        mode (:address-mode instruction)
        operand (:operand instruction)]
    (case mode
      :zeropage   operand
      :zeropagex  (zeropage-reg8 operand (:x system))
      :zeropagey  (zeropage-reg8 operand (:y system))
      :absolute   operand
      :absolutex  (absolute-reg16 operand (:x system))
      :absolutey  (absolute-reg16 operand (:y system))
      :indirect   (indirect-address mem operand)
      :indirectx  (indirect-x system operand)
      :indirecty  (indirect-y system operand))))

(defn read-from-memory
  "Reads a value from memory for a given instruction."
  [system instruction]
  (->> (resolve-address system instruction)
       (get (:mem system))))

