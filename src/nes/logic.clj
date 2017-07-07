(ns nes.logic)

(defn bit-opfn [system m]
  (let [result (bit-and m (:acc system))]
    (-> system
        (assoc :zero-flag (= result 0))
        (assoc :sign-flag (bit-test m 7))
        (assoc :overflow-flag (bit-test m 6)))))

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
