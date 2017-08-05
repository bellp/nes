(ns nes.logic
  (:require [nes.memory :as mem]
            [nes.arithmetic :as math]))

(defn bit-opfn [system m]
  (let [result (bit-and m (:acc system))]
    (-> system
        (assoc :zero-flag (= result 0))
        (assoc :sign-flag (bit-test m 7))
        (assoc :overflow-flag (bit-test m 6)))))

(defn- bitop-opfn [system m bit-fn]
  (let [result (bit-fn m (:acc system))]
    (-> system
        (assoc :acc result)
        (assoc :zero-flag (= result 0))
        (assoc :sign-flag (bit-test result 7)))))

(defn and-opfn [system m]
  (bitop-opfn system m bit-and))

(defn ora-opfn [system m]
  (bitop-opfn system m bit-or))

(defn eor-opfn [system m]
  (bitop-opfn system m bit-xor))

(defn asl-opfn [system addr]
  (let [value (mem/read8 system addr)
        shifted (bit-and 0xFF (bit-shift-left value 1))]
    (-> system
        (assoc :zero-flag (= shifted 0))
        (assoc :carry-flag (bit-test value 7))
        (assoc :sign-flag (bit-test shifted 7))
        (mem/write8 addr shifted))))

(defn lsr-opfn [system addr]
  (let [value (mem/read8 system addr)
        shifted (bit-and 0xFF (bit-shift-right value 1))]
    (-> system
        (assoc :zero-flag (= shifted 0))
        (assoc :carry-flag (bit-test value 0))
        (assoc :sign-flag false)
        (mem/write8 addr shifted))))

(defn rol-opfn [system addr]
  (let [value (mem/read8 system addr)
        shifted (bit-and 0xFF (bit-shift-left value 1))
        shifted-with-carry (if (:carry-flag system)
                              (bit-or shifted 0x01)
                              shifted)]
    (-> system
        (assoc :zero-flag (= shifted-with-carry 0))
        (assoc :carry-flag (bit-test value 7))
        (assoc :sign-flag (bit-test shifted-with-carry 7))
        (mem/write8 addr shifted-with-carry))))

(defn ror-opfn [system addr]
  (let [value (mem/read8 system addr)
        shifted (bit-and 0xFF (bit-shift-right value 1))
        shifted-with-carry (if (:carry-flag system)
                              (bit-or shifted 0x80)
                              shifted)]
    (-> system
        (assoc :zero-flag (= shifted-with-carry 0))
        (assoc :carry-flag (bit-test value 0))
        (assoc :sign-flag (bit-test shifted-with-carry 7))
        (mem/write8 addr shifted-with-carry))))

(defn slo-opfn [system addr]
  (let [after-asl-sys (asl-opfn system addr)
        m (mem/read8 after-asl-sys addr)]
    (ora-opfn after-asl-sys m)))

(defn rla-opfn [system addr]
  (let [after-rol-sys (rol-opfn system addr)
        m (mem/read8 after-rol-sys addr)]
    (and-opfn after-rol-sys m)))

(defn sre-opfn [system addr]
  (let [after-lsr-sys (lsr-opfn system addr)
        m (mem/read8 after-lsr-sys addr)]
    (eor-opfn after-lsr-sys m)))

(defn rra-opfn [system addr]
  (let [after-ror-sys (ror-opfn system addr)
        m (mem/read8 after-ror-sys addr)]
    (math/adc-opfn after-ror-sys m)))
