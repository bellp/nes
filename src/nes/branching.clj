(ns nes.branching
  (:require [nes.memory :as mem]))

(defn jmp-opfn
  [system m]
  (assoc system :pc m))

(defn branch [system relative]
  (let [offset (if (bit-test relative 7)
                  (-> relative bit-not (bit-and 0xFF) inc (* -1))
                  relative)
        target-address (bit-and 0xFFFF (+ (:pc system) offset))
        page-cross-cycle (if (mem/same-page? (:pc system) target-address)
                          0
                          1)
        extra-cycles (+ 1 page-cross-cycle)]
    (-> system
        (update :cycle-count #(+ % extra-cycles))
        (assoc :pc target-address))))

(defn branch-on [system m condition]
  (if condition
    (branch system m)
    system))

(defn bcc-opfn
  [system m]
  (branch-on system m (not (:carry-flag system))))

(defn bcs-opfn
  [system m]
  (branch-on system m (:carry-flag system)))

(defn beq-opfn
  [system m]
  (branch-on system m (:zero-flag system)))

(defn bne-opfn
  [system m]
  (branch-on system m (not (:zero-flag system))))

(defn bmi-opfn
  [system m]
  (branch-on system m (:sign-flag system)))

(defn bpl-opfn
  [system m]
  (branch-on system m (not (:sign-flag system))))

(defn bvc-opfn
  [system m]
  (branch-on system m (not (:overflow-flag system))))

(defn bvs-opfn
  [system m]
  (branch-on system m (:overflow-flag system)))

(defn jsr-opfn
  [system addr]
  ; (println (format "Pushing address %04X onto stack" (:pc system)))
  (-> system
      (mem/push16 (dec (:pc system)))
      (assoc :pc addr)))

(defn rts-opfn
  [system _]
  (let [value (->> (:sp system)
                   (inc)
                   (bit-and 0xFF)
                   (bit-or 0x100)
                   (mem/read16 system)
                   (inc))]

        ; _ (println (format "Pulled address %04X from the stack" value))]
    (-> system
        (assoc :pc value)
        (update :sp #(+ % 2)))))

