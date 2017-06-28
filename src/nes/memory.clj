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

(defn get-operand
  "Gets an operand form a given address given a
  size (0, 1, or 2 bytes)"
  [mem addr size]
  (case size
    0 nil
    1 (get mem addr)
    2 (combine-bytes
        (get mem (inc addr))
        (get mem addr))))

(defn indirect
  [mem operand]
  (let [msb (get mem (bit-and (inc operand) 0xFFFF))
        lsb (get mem (bit-and operand 0xFFFF))]
    (combine-bytes msb lsb)))

(defn address
  "Calculates the address using the specified mode"
  [system mode operand]
  (case mode
    :relative 0
    :zeropage (bit-and operand 0xFF)
    :zeropagex (-> operand
                   (+ (get-in system [:cpu :x]))
                   (bit-and 0xFF))
    :zeropagey (-> operand
                   (+ (get-in system [:cpu :y]))
                   (bit-and 0xFF))
    :absolute (bit-and operand 0xFFFF)
    :absolutex (-> operand
                   (+ (get-in system [:cpu :x]))
                   (bit-and 0xFFFF))
    :absolutey (-> operand
                   (+ (get-in system [:cpu :y]))
                   (bit-and 0xFFFF))
    :indirect (-> system
                  (:mem)
                  (indirect operand))
    :indirectx (-> system
                  (:mem)
                  (indirect
                    (bit-and
                      (+ operand
                         (get-in system [:cpu :x]))
                      0xFF)))
    :indirecty (-> system
                   (:mem)
                   (indirect operand)
                   (+ (get-in system [:cpu :y]))
                   (bit-and 0xFFFF))))
