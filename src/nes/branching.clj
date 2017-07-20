(ns nes.branching
  (:require [nes.memory :as mem]))

(defn jmp-opfn
  [system m]
  (assoc system :pc m))

(defn branch [system relative]
  (let [offset (if (bit-test relative 7)
                  (-> relative bit-not (bit-and 0xFF) inc (* -1))
                  relative)]
    (-> system
        (update :cycle-count inc)
        (assoc :pc (bit-and 0xFFFF
                            (+ (:pc system) offset))))))

(defn- bcc
  [system m condition]
  (if condition
    (branch system m)
    system))

(defn bcc-opfn
  [system m]
  (bcc system m (not (:carry-flag system))))

(defn bcs-opfn
  [system m]
  (bcc system m (:carry-flag system)))

(defn beq-opfn
  [system m]
  (bcc system m (:zero-flag system)))

(defn bne-opfn
  [system m]
  (bcc system m (not (:zero-flag system))))

(defn bmi-opfn
  [system m]
  (bcc system m (:sign-flag system)))

(defn bpl-opfn
  [system m]
  (bcc system m (not (:sign-flag system))))

(defn bvc-opfn
  [system m]
  (bcc system m (not (:overflow-flag system))))

(defn bvs-opfn
  [system m]
  (bcc system m (:overflow-flag system)))

(defn jsr-opfn
  [system addr]
  (-> system
      (mem/push16 (+ 2 (:pc system)))
      (assoc :pc addr)))

(defn rts-opfn
  [system _]
  (let [value (mem/read16 system (bit-or 0x100 (dec (:sp system))))]
    (-> system
        (assoc :pc (inc value))
        (update :sp #(+ % 2)))))

