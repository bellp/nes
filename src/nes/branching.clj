(ns nes.branching
  (:use [nes.system]))

(defn jmp
  [system m]
  (assoc-in system [:cpu :pc] m))
