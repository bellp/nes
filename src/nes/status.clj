(ns nes.status
  (:require [nes.memory :as mem]))

(defn get-status [system]
  (let [set-flag (fn [status flag pos]
                   (if flag (bit-set status pos) status))]
    (-> 0x00
        (set-flag (:carry-flag system) 0)
        (set-flag (:zero-flag system) 1)
        (set-flag (:int-flag system) 2)
        (set-flag (:dec-flag system) 3)
        (set-flag (:brk-flag system) 4)
        (set-flag (:unused-flag system) 5)
        (set-flag (:overflow-flag system) 6)
        (set-flag (:sign-flag system) 7))))

(defn update-status [system sr]
  (-> system
      (assoc :carry-flag (bit-test sr 0))
      (assoc :zero-flag (bit-test sr 1))
      (assoc :int-flag (bit-test sr 2))
      (assoc :dec-flag (bit-test sr 3))
      (assoc :unused-flag true)
      (assoc :overflow-flag (bit-test sr 6))
      (assoc :sign-flag (bit-test sr 7))))

(defn clc-opfn [system _]
  (assoc system :carry-flag false))

(defn cld-opfn [system _]
  (assoc system :dec-flag false))

(defn cli-opfn [system _]
  (assoc system :int-flag false))

(defn clv-opfn [system _]
  (assoc system :overflow-flag false))

(defn sec-opfn [system _]
  (assoc system :carry-flag true))

(defn sed-opfn [system _]
  (assoc system :dec-flag true))

(defn sei-opfn [system _]
  (assoc system :int-flag true))

(defn nop-opfn [system _]
  system)

(defn brk-opfn [system _]
  (-> system
      (mem/push16 (:pc system))
      (mem/push8 (bit-or 0x10 (get-status system)))
      (assoc :pc (mem/read16 system 0xFFFE))))

(defn rti-opfn [system _]
  (let [sr-address (bit-or 0x100 (inc (:sp system)))
        pc-address (bit-or 0x100 (+ (:sp system) 2))
        [status after-sys] (mem/read8 system sr-address)]
    (-> after-sys
        (update-status status)
        (assoc :pc (mem/read16 system pc-address))
        (update :sp #(+ % 3)))))

(defn pha-opfn
  [system _]
  (mem/push8 system (:acc system)))

(defn php-opfn
  [system _]
  (mem/push8 system (bit-or 0x30 (get-status system))))

(defn pla-opfn
  [system _]
  (let [value (mem/read-last-pushed-byte system)]
    (-> system
        (assoc :acc value)
        (assoc :zero-flag (= value 0))
        (assoc :sign-flag (bit-test value 7))
        (update :sp inc))))

(defn plp-opfn
  [system _]
  (let [value (mem/read-last-pushed-byte system)]
    (-> system
        (update-status value)
        (update :sp inc))))




