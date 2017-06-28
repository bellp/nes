(ns nes.arithmetic
    (:require [nes.cpu :refer :all]))

(defn op-adc
  [cpu m]
  (let [carry (bit-and (:status cpu) 0x01)
        acc (:acc cpu)
        sum (+ acc m carry)
        byte-sum (bit-and sum 0xFF)]
      (-> cpu
        (assoc :acc byte-sum)
        (set-flag :carry (> sum 0xFF))
        (set-flag :zero (= byte-sum 0))
        (set-flag :sign (bit-test byte-sum 7))
        (set-flag :overflow
        (not
          (=
            (bit-test byte-sum 7)
            (bit-test acc 7)))))))

(defn adc
  [system m]
  (op-adc (:cpu system) m))

(defn op-sbc [cpu m]
    (let [carry (bit-and (:status cpu) 0x01)
          acc (:acc cpu)
          sum (- acc m (bit-flip carry 0))
          byte-sum (bit-and sum 0xFF)]
          (-> cpu
              (assoc :acc byte-sum)
              (set-flag :carry (<= sum 0xFF))
              (set-flag :zero (= byte-sum 0))
              (set-flag :sign (bit-test byte-sum 7))
              (set-flag :overflow
                  (and
                      (bit-test (bit-xor acc m) 7)
                      (bit-test (bit-xor acc byte-sum) 7))))))

(defn change-by-one
    [op value]
    (-> value
        (op)
        (bit-and 0xFF)))

(defn update-by-one-flags
    "Update cpu flags after incrementing/decrementing"
    [cpu value]
    (-> cpu
        (set-flag :zero (= value 0))
        (set-flag :sign (bit-test value 7))))

(defn change-memory-by-one
    "Used by increment/decrement functions to change values in memory"
    [cpu op mem addr]
    (let [result (change-by-one op (mem addr))]
      { :cpu (update-by-one-flags cpu result)
        :mem (assoc mem addr result) }))

(defn change-register-by-one
    "Used by increment/decrement functions to change values in a register"
    [cpu op register]
    (let [result (change-by-one op (register cpu))]
    (-> cpu
        (update-by-one-flags result)
        (assoc register result))))

(defn op-dec [cpu mem addr]
    (change-memory-by-one cpu dec mem addr))

(defn op-inc [cpu mem addr]
    (change-memory-by-one cpu inc mem addr))

(defn op-dex [cpu]
    (change-register-by-one cpu dec :x))

(defn op-dey [cpu]
    (change-register-by-one cpu dec :y))

(defn op-inx [cpu]
    (change-register-by-one cpu inc :x))

(defn op-iny [cpu]
    (change-register-by-one cpu inc :y))
