(ns nes.arithmetic
  (require [nes.mapper :as mapper]
           [nes.memory :as mem]))

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
          (and
            (= 0 (bit-and 0x80 (bit-xor acc m)))
            (not (= 0 (bit-and 0x80 (bit-xor acc byte-sum)))))))))

(defn sbc-opfn
  [system m]
  (let [carry (if (:carry-flag system) 1 0)
        acc (:acc system)
        diff (- acc m (bit-flip carry 0))
        byte-diff (bit-and diff 0xFF)
        overflow (and
                   (not (= 0 (bit-and 0x80 (bit-xor acc m))))
                   (not (= 0 (bit-and 0x80 (bit-xor acc byte-diff)))))]
    (-> system
      (assoc :acc byte-diff)
      (assoc :carry-flag (>= diff 0))
      (assoc :zero-flag (= byte-diff 0))
      (assoc :sign-flag (bit-test byte-diff 7))
      (assoc :overflow-flag overflow))))

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
  (let [[value after-sys] (mapper/read8 system addr)
        result (change-by-one op value)]
    (-> after-sys
        (update-by-one-flags result)
        (mapper/write8 addr result))))

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

(defn dex-opfn [system _]
  (change-register-by-one system dec :x))

(defn dey-opfn [system _]
  (change-register-by-one system dec :y))

(defn inx-opfn [system _]
  (change-register-by-one system inc :x))

(defn iny-opfn [system _]
  (change-register-by-one system inc :y))

(defn cmp-reg [system reg8 m]
  (let [diff (bit-and (- reg8 m) 0xFF)]
    (-> system
        (assoc :carry-flag (>= reg8 m))
        (assoc :zero-flag (= reg8 m))
        (assoc :sign-flag (bit-test diff 7)))))

(defn cmp-opfn [system m]
  (cmp-reg system (:acc system) m))

(defn cpx-opfn [system m]
  (cmp-reg system (:x system) m))

(defn cpy-opfn [system m]
  (cmp-reg system (:y system) m))

(defn dcp-opfn [system addr]
  (let [[before-m sys-after-first-read] (mem/read8 system addr)
        after-dec (dec-opfn sys-after-first-read addr)
        [after-m sys-after-2nd-read] (mem/read8 after-dec addr)]
    (cmp-reg sys-after-2nd-read (:acc sys-after-2nd-read) after-m)))

(defn ins-opfn [system addr]
  (let [inc-sys (inc-opfn system addr)
        [m sys-after] (mem/read8 inc-sys addr)]
    (sbc-opfn sys-after m)))

