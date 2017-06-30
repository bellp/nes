(ns nes.memory
  (:use [nes.opcodes]))

(defn new-memory
  "Creates a new 64kb memory space."
  []
  (->> (repeat 65536 0x00)
       (vec)))

(defn add-8
  [x y]
  (bit-and
    (+ x y)
    0xFF))

(defn add-16
  [x y]
  (bit-and
    (+ x y)
    0xFFFF))

(defn combine-bytes
  [msb lsb]
  (bit-or
    (bit-shift-left msb 8)
    lsb))

(defn read-value
  "Gets an operand form a given address given a
  size (0, 1, or 2 bytes)"
  [mem addr size]
  (case size
    0 nil
    1 (get mem addr)
    2 (combine-bytes
        (get mem (inc addr))
        (get mem addr))))

(defn get-operand
  [system instruction]
  (let [operand-size (get operand-sizes (:address-mode instruction))
        pc (:pc system)]
    (read-value (:mem system) (inc pc) operand-size)))

(defn indirect
  [mem operand]
  (let [msb (get mem (bit-and (inc operand) 0xFFFF))
        lsb (get mem (bit-and operand 0xFFFF))]
    (combine-bytes msb lsb)))

(defn zeropage-reg8
  [system operand reg]
    (let [src (bit-and (+ reg operand) 0xFF)]
      (get-in system [:mem src])))

(defn absolute-reg16
  [system operand reg]
    (let [src (bit-and (+ reg operand) 0xFFFF)]
      (get-in system [:mem src])))

(defn address
  "Calculates the address using the specified mode"
  [system mode operand]
  (case mode
    :relative (throw (Exception. "TODO"))
    :zeropage (bit-and operand 0xFF)
    :zeropagex (zeropage-reg8 system operand (:x system))
    :zeropagey (zeropage-reg8 system operand (:y system))
    :absolute (bit-and operand 0xFFFF)
    :absolutex (absolute-reg16 system operand (:x system))
    :absolutey (absolute-reg16 system operand (:y system))
    :indirect (-> system
                  (:mem)
                  (indirect operand))
    :indirectx (-> system
                  (:mem)
                  (indirect
                    (bit-and
                      (+ operand
                        (get system :x))
                      0xFF)))
    :indirecty (-> system
                   (:mem)
                   (indirect operand)
                   (+ (get system :y))
                   (bit-and 0xFFFF))))
