(ns nes.branching
  (:use [nes.system]))

(defn jmp-opfn
  [system m]
  (assoc system :pc m))

(defn bcc-opfn
  [system m]
  (if (get system :carry-flag)
    system
    (-> system
        (assoc :pc m)
        (update :cycle-count inc))))

(defn bcs-opfn
  [system m]
  (if (get system :carry-flag)
    (-> system
      (assoc :pc m)
      (update :cycle-count inc))
    system))