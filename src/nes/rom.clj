(ns nes.rom)

(defn read-rom
  [path]
  (let [file-stream (->> (new java.io.File path)
                         (new java.io.FileInputStream))
        buffer (make-array Byte/TYPE (.available file-stream))
        bytes (.read file-stream buffer)]
    (vec buffer)))

(defn read-header [rom]
  {:preamble (->> rom (take 3) (map char) (reduce str))
   :prg-rom-units (rom 4)
   :chr-rom-units (rom 5)
   :trainer? (bit-test (rom 6) 2)})

(defn read-prg [rom header]
  (->> rom
       (drop 16) ; Header
       (drop (if (:trainer? header)
               (512)
               0))
       (take (* (:prg-rom-units header)16384))
       (vec)))

(defn write-into [mem start data]
  (loop [i 0 m mem]
    (if (>= i (count data))
      m
      (recur (inc i) (assoc m (+ i start) (data i))))))






