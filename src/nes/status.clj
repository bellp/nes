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
      (assoc :brk-flag true)
      (mem/push8 (get-status system))
      (assoc :pc (mem/read16 system 0xFFFE))))



