(ns nes.mapper
  (require [nes.rom :as rom]
           [nes.debug :as debug]
           [nes.ppu :as ppu]))

(defn test-write8
  [system addr value]
  (assoc-in system [:mapper :mem addr] value))

(defn test-read8
  [system addr]
  [(get-in system [:mapper :mem addr]) system])

(defn test-mapper
  "This is a mapper intended only for testing/debugging of the CPU. It's just a flat 64k of RAM
  ...no registers, CHR/PRG banks, mirroring, etc."
  []
  {:read-fn test-read8
   :write-fn test-write8
   :mem (vec (repeat 0x10000 0x00))}) ; 64k RAM

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

(defn write-mem8
  [system addr value]
  (if (< addr 0x2000)
    (assoc-in system [:mapper :mem (bit-and addr 0x7FF)] value)
    system))

(defn read-mem8
  [system addr]
  [(get-in system [:mapper :mem (bit-and addr 0x7FF)]) system])

; No Mapper (iNES Mapper 0)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn mapper0-write8
  [system addr value]
  (cond
   (< addr 0x2000) (assoc-in system [:mapper :mem (bit-and addr 0x7FF)] value)
   :else system))

(defn mapper0-read8
  [system addr]
  (cond
    (< addr 0x2000) (read-mem8 system addr) ; RAM
    (< addr 0x4000) (ppu/read8 system addr)
    (>= addr 0x8000) [(get-in system [:mapper :8000 (bit-and addr 0x3FFF)]) system]
    (>= addr 0xC000) [(get-in system [:mapper :c000 (bit-and addr 0x3FFF)]) system]
    :else (throw (Exception. (format "Address %04X not supported yet." addr)))))

(defn mapper0
  "This is the simplest NES mapper... which is no mapper at all.
  Does not allow re-mapping of PRG or CHR banks."
  [rom]
  (let [num-prg-banks (get-in rom [:header :num-prg-rom-banks])
        c000-bank (if (= num-prg-banks 1)
                    (get-in rom [:prg-banks 0])
                    (get-in rom [:prg-banks 1]))]
    {:read-fn mapper0-read8
     :write-fn mapper0-write8
     :mem (vec (repeat 0x800 0x00)) ; 2k RAM
     :8000 (get-in rom [:prg-banks 0])
     :c000 c000-bank
     :rom rom}))

(defn load-mapper
  [rom]
  (let [mapper-id (get-in rom [:header :mapper])]
    (case mapper-id
      0 (mapper0 rom)
      (throw (Exception. (format "Mapper not found: %d" mapper-id))))))

