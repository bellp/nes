(ns nes.arithmetic
  (:require [nes.cpu :refer :all]))

(defn adc-opfn
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

(defn sbc-opfn
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

(defn- change-register-by-one
  "Used by increment/decrement functions to change values in a register"
  [system op register]
  (let [result (change-by-one op (register system))]
    (-> system
        (update-by-one-flags result)
        (assoc register result))))

(defn dec-opfn [system addr]
  (change-memory-by-one system dec addr))

(defn inc-opfn [system addr]
  (change-memory-by-one system inc addr))

(defn dex-opfn [system]
  (change-register-by-one system dec :x))

(defn dey-opfn [system]
  (change-register-by-one system dec :y))

(defn inx-opfn [system]
  (change-register-by-one system inc :x))

(defn iny-opfn [system]
  (change-register-by-one system inc :y))

(defn cmp-opfn [system m]
  (cmp-reg system m (:acc system)))

(defn cpx-opfn [system m]
  (cmp-reg system m (:x system)))

(defn cpy-opfn [system m]
  (cmp-reg system m (:y system)))

(defn cmp-reg [system reg8 m]
  (let [diff (bit-and (- reg8 m) 0xFF)]
    (-> system
        (assoc :carry-flag (>= reg8 m))
        (assoc :zero-flag (= reg8 m))
        (assoc :sign-flag (bit-test diff 7)))))
