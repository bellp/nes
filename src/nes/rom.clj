(ns nes.rom
  (require [nes.debug :as debug]))

(defn read-file
  [path]
  (let [file-stream (->> (new java.io.File path)
                         (new java.io.FileInputStream))
        buffer (make-array Byte/TYPE (.available file-stream))]
    (.read file-stream buffer)
    (->> buffer
         (map #(bit-and 0xFF %))
         (vec))))

(defn read-header [rom]
  {:preamble (->> rom (take 3) (map char) (reduce str))
   :num-prg-rom-banks (rom 4)
   :num-chr-rom-banks (rom 5)
   :mirroring (if (bit-test (rom 6) 0) :vertical :horizontal)
   :battery? (bit-test (rom 6) 1)
   :trainer? (bit-test (rom 6) 2)
   :mapper (bit-or
             (bit-shift-right (bit-and 0xF0 (rom 6)) 4)
             (bit-and 0xF0 (rom 7)))})

(defn read-prg-banks [rom header]
  (let [prg-bytes (->> rom
                       (drop 16) ; Header
                       (drop (if (:trainer? header) 512 0))
                       (take (* (:num-prg-rom-banks header) 0x4000))
                       (vec))]
    (->> (range (:num-prg-rom-banks header))
         (map (fn [n]
                {n (->> prg-bytes
                        (drop (* n 0x4000))
                        (take 0x4000)
                        (vec))}))
         (reduce merge))))

(defn read-chr-banks [rom header]
  (let [chr-bytes (->> rom
                       (drop 16) ; Header
                       (drop (if (:trainer? header) 512 0))
                       (drop (* (:num-prg-rom-banks header) 0x4000))
                       (vec))]
    (->> (range (:num-chr-rom-banks header))
         (map (fn [n]
                {n (->> chr-bytes
                        (drop (* n 0x2000))
                        (take 0x2000)
                        (vec))}))
         (reduce merge))))

(defn read-rom
  [rom]
  (let [header (read-header rom)]
    {:header header
     :prg-banks (read-prg-banks rom header)
     :chr-banks (read-chr-banks rom header)}))

(defn write-into [mem start data]
  (loop [i 0 m mem]
    (if (>= i (count data))
      m
      (recur (inc i) (assoc m (+ i start) (data i))))))

