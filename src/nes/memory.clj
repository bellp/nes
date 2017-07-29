(ns nes.memory)

(defn new-memory
  "Creates a new 64kb memory space."
  []
  (->> (repeat 65536 0x00)
       (vec)))

(defn most-significant-byte [value]
  (-> value
      (bit-shift-right 8)
      (bit-and 0xFF)))

(defn combine-bytes [msb lsb]
  (-> msb
      (bit-shift-left 8)
      (bit-or lsb)))

(defn read-last-pushed-byte
  [system]
  (get-in system [:mem (bit-or 0x100 (inc (:sp system)))]))

(defn push8 [system value]
  (-> system
      (assoc-in [:mem (bit-or 0x100 (:sp system))] value)
      (update :sp dec)))

(defn push16 [system value]
  (let [msb (most-significant-byte value)
        lsb (bit-and 0xFF value)
        top (bit-or (:sp system) 0x100)
        top-1 (dec (bit-or (:sp system) 0x100))]
    (-> system
        (assoc-in [:mem top] msb)
        (assoc-in [:mem top-1] lsb)
        (update :sp (fn [sp] (bit-and 0xFF (- sp 2)))))))

(defn read16
  [system address]
  (let [mem (:mem system)
        lsb (get mem address)
        msb (get mem (inc address))]
    (combine-bytes msb lsb)))

(defn write16 [system address value]
  (let [msb (most-significant-byte value)
        lsb (bit-and value 0xFF)]
    (-> system
        (assoc-in [:mem address] lsb)
        (assoc-in [:mem (bit-and 0xFFFF (inc address))] msb))))

(defn read8
  "Reads an 8-bit value from a given address (or accumulator)"
  [system address]
  (case address
    :accumulator (:acc system)
    (get-in system [:mem address])))

(defn write8 [system address value]
  (case address
    :accumulator (assoc system :acc (bit-and 0xFF value))
    (assoc-in system [:mem address] (bit-and 0xFF value))))

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
      :accumulator :accumulator
      :immediate   nil
      :implied     nil
      :relative    operand
      :zeropage    operand
      :zeropagex   (zeropage-reg8 operand (:x system))
      :zeropagey   (zeropage-reg8 operand (:y system))
      :absolute    operand
      :absolutex   (absolute-reg16 operand (:x system))
      :absolutey   (absolute-reg16 operand (:y system))
      :indirect    (indirect-address mem operand)
      :indirectx   (indirect-x system operand)
      :indirecty   (indirect-y system operand))))

(defn read-from-memory
  "Reads a value from memory for a given instruction."
  [system instruction address]
  (let [mode (:address-mode instruction)]
    (case mode
      :accumulator nil
      :implied nil
      :relative (:operand instruction)
      :immediate (:operand instruction)
      (get (:mem system) address))))

