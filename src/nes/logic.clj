(ns nes.logic)

(defn and-opfn [system m]
  (bitop-opfn system m bit-and))

(defn ora-opfn [system m]
  (bitop-opfn system m bit-or))

(defn eor-opfn [system m]
  (bitop-opfn system m bit-xor))

(defn- bitop-opfn [system m bit-fn]
  (let [result (bit-fn m (:acc system))]
    (-> system
        (assoc :acc result)
        (assoc :zero-flag (= result 0))
        (assoc :sign-flag (bit-test result 7)))))
