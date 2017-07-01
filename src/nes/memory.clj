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
  (let [mem (:mem system)
        addr (-> mem
                (indirect-address operand)
                (+ (:y system))
                (bit-and 0xFFFF))]
    (get mem addr)))

(defn indirect-x
  [system operand]
  (let [mem (:mem system)
        x-offset (bit-and (+ operand (:x system)) 0xFF)
        addr (-> mem
                (indirect-address x-offset))]
    (get mem addr)))

(defn zeropage-reg8
  [system operand reg]
    (let [src (bit-and (+ reg operand) 0xFF)]
      (get-in system [:mem src])))

(defn absolute-reg16
  [system operand reg]
    (let [src (bit-and (+ reg operand) 0xFFFF)]
      (get-in system [:mem src])))

(defn read-from-memory
  "Reads a value from memory for a given instruction."
  [system instruction]
  (let [mem (:mem system)
        mode (:address-mode instruction)
        operand (:operand instruction)]
    (case mode
      :relative (throw (Exception. "TODO"))
      :zeropage (get-in system [:mem operand])
      :zeropagex (zeropage-reg8 system operand (:x system))
      :zeropagey (zeropage-reg8 system operand (:y system))
      :absolute (get-in system [:mem operand])
      :absolutex (absolute-reg16 system operand (:x system))
      :absolutey (absolute-reg16 system operand (:y system))
      :indirect (get mem (indirect-address mem operand))
      :indirectx (indirect-x system operand)
      :indirecty (indirect-y system operand))))