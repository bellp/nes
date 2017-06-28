(ns nes.cpu)

(defn new-cpu
  "Create a new 6502 cpu with a clean state"
  []
  { :acc    0x00
    :x      0x00
    :y      0x00
    :status 0x20
    :cycles 0
    :pc     0x00
    :sp     0x00 })

(defn flag-set?
  "Returns whether or not flag is set.
  Available flags are :carry, :zero, :overflow,
  :int, :dec, :unused, :brk, :sign"
  [cpu flag]
  (let [status (:status cpu)]
    (case flag
      :carry    (bit-test status 0)
      :zero     (bit-test status 1)
      :overflow (bit-test status 2)
      :int      (bit-test status 3)
      :dec      (bit-test status 4)
      :unused   (bit-test status 5)
      :brk      (bit-test status 6)
      :sign     (bit-test status 7))))

(defn set-flag
  "Returns a new CPU object with the specified flag set to a new value"
  [cpu flag state]
  (let [status (:status cpu)
        op (if state bit-set bit-clear)]
    (assoc cpu :status
      (case flag
          :carry    (op status 0)
          :zero     (op status 1)
          :overflow (op status 2)
          :int      (op status 3)
          :dec      (op status 4)
          :unused   (op status 5)
          :brk      (op status 6)
          :sign     (op status 7)))))

(defn add-bytes
  "Adds two bytes together, ensuring no values over 255 and that the
  resulting type is a byte"
  [b1 b2]
  (byte (bit-and (+ b1 b2) 0xFF)))

(defn add-word
  "Adds a 16-bit word and a byte. Ensuring the value doesn't go beyond 0xFFFF"
  [word byte]
  (bit-and (+ word byte) 0xFFFF))
