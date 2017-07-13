(ns nes.branching
  (:use [nes.system]))

(defn jmp-opfn
  [system m]
  (assoc system :pc m))

(defn branch [system relative]
  (let [offset-func (if (bit-test relative 7) - +)]
    (assoc system :pc (bit-and
                         0xFFFF
                         (offset-func (:pc system) (bit-and 0x7F relative))))))

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