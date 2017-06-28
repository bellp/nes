(ns nes.arithmetic
    (:require [nes.cpu :refer :all]))

(defn adc
  [system m]
  (let [carry (if (get system :carry-flag) 1 0)
        acc (:acc system)
        sum (+ acc m carry)
        byte-sum (bit-and sum 0xFF)]
      (-> system
          (assoc :acc byte-sum)
          (assoc :carry-flag (> sum 0xFF))
          (assoc :zero-flag (= byte-sum 0))
          (assoc :sign-flag (bit-test byte-sum 7))
          (assoc :overflow-flag
            (not
              (=
                (bit-test byte-sum 7)
                (bit-test acc 7)))))))

(defn sbc
  [system m]
    (let [carry (if (get system :carry-flag) 1 0)
          acc (:acc system)
          sum (- acc m (bit-flip carry 0))
          byte-sum (bit-and sum 0xFF)]
          (-> system
              (assoc :acc byte-sum)
              (assoc :carry-flag (<= sum 0xFF))
              (assoc :zero-flag (= byte-sum 0))
              (assoc :sign-flag (bit-test byte-sum 7))
              (assoc :overflow-flag
                  (and
                      (bit-test (bit-xor acc m) 7)
                      (bit-test (bit-xor acc byte-sum) 7))))))

(defn change-by-one
    [op value]
    (-> value
        (op)
        (bit-and 0xFF)))

(defn update-by-one-flags
    "Update system flags after incrementing/decrementing"
    [system value]
    (-> system
        (assoc :zero-flag (= value 0))
        (assoc :sign-flag (bit-test value 7))))

(defn change-memory-by-one
    "Used by increment/decrement functions to change values in memory"
    [system op addr]
    (let [mem (:mem system)
          result (change-by-one op (mem addr))]
      (-> system
          (update-by-one-flags result)
          (assoc-in [:mem addr] result))))

(defn change-register-by-one
    "Used by increment/decrement functions to change values in a register"
    [system op register]
    (let [result (change-by-one op (register system))]
    (-> system
        (update-by-one-flags result)
        (assoc register result))))

(defn op-dec [system addr]
    (change-memory-by-one system dec addr))

(defn op-inc [system addr]
    (change-memory-by-one system inc addr))

(defn op-dex [system]
    (change-register-by-one system dec :x))

(defn op-dey [system]
    (change-register-by-one system dec :y))

(defn op-inx [system]
    (change-register-by-one system inc :x))

(defn op-iny [system]
    (change-register-by-one system inc :y))
