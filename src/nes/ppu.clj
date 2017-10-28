(ns nes.ppu)

(defn new
  []
  {:vblank false
   :sprite-size :8x8
   :sprite-0-hit false
   :vram (vec (repeat 0x4000 0x00)) ; 16k VRAM
   :addr-inc-by 1
   :addr 0x0000
   :name-table-addr 0x0000
   :sprite-pattern-table 0x0000
   :background-pattern-table 0x0000
   :scroll :0x00})

(defn read8
  "Returns a tuple (vector) with [return-value system], since reading the PPU can affect
  the state of the system"
  [system addr]
  [0x00 system])

(defn write8
  "takes an address within range $2000-$2007 (or a mirrored address) and writes value"
  [system addr value]
  system)

(defn write-2000
  [ppu value]
  (merge
    ppu
    {:name-table-addr (+ 0x2000
                         (* 0x400 (bit-and value 0x03)))
     :addr-inc-by (if (bit-test value 2) 32 1)
     :sprite-pattern-table (if (bit-test value 3) 0x1000 0x0000)
     :background-pattern-table (if (bit-test value 4) 0x1000 0x0000)
     :sprite-size (if (bit-test value 5) :8x16 :8x8)
     :vblank (bit-test value 7)}))

(defn write-2001
  [ppu value]
  ppu)

