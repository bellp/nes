(ns nes.transfer
  (:require [nes.memory :as mem]))

(defn load-register [system value reg]
  (-> system
      (assoc reg value)
      (assoc :sign-flag (bit-test value 7))
      (assoc :zero-flag (= value 0))))

(defn lda-opfn
  [system m]
  (load-register system m :acc))

(defn ldx-opfn
  [system m]
  (load-register system m :x))

(defn ldy-opfn
  [system m]
  (load-register system m :y))

(defn tax-opfn
  [system _]
  (load-register system (:acc system) :x))

(defn tay-opfn
  [system _]
  (load-register system (:acc system) :y))

(defn tsx-opfn
  [system _]
  (load-register system (:sp system) :x))

(defn txa-opfn
  [system _]
  (load-register system (:x system) :acc))

(defn txs-opfn
  [system _]
  (assoc system :sp (:x system)))

(defn tya-opfn
  [system _]
  (load-register system (:y system) :acc))

(defn store-register [system register addr]
  (assoc-in system [:mem addr] (register system)))

(defn sta-opfn
  [system addr]
  (store-register system :acc addr))

(defn stx-opfn
  [system addr]
  (store-register system :x addr))

(defn sty-opfn
  [system addr]
  (store-register system :y addr))
