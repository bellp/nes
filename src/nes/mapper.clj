(ns nes.mapper
  (require [nes.system]))

(defn test-write8
  [system addr value]
  (assoc-in system [:mapper :mem addr] value))

(defn test-read8
  [system addr]
  (get-in system [:mapper :mem addr]))

(def test-mapper
  "This is a mapper intended only for testing/debugging of the CPU. It's just a flat 64k of RAM
  ...no registers, CHR/PRG banks, mirroring, etc."
  {:read-fn test-read8
   :write-fn test-write8
   :mem (vec (repeat 65536 0x00))})

(defn mapper0
  [system]
  (assoc system {}))

(defn read8
  [system addr]
  (let [read-fn (get-in system [:mapper :read-fn])]
    (read-fn system addr)))

(defn write8
  [system addr value]
  (let [write-fn (get-in system [:mapper :write-fn])]
    (write-fn system addr value)))
