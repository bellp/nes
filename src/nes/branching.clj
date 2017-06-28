(ns nes.branching
  (:use [nes.system]))

(defn jmp
  [system m]
  (assoc system :pc m))

(defn bcc
  [system m]
  (if (get system :carry-flag)
    system
    (assoc system :pc m)))

(defn bcs
  [system m]
  (if (get system :carry-flag)
    (assoc system :pc m)
     system))


