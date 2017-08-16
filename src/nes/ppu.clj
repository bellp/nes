(ns nes.ppu)

(defn new
  []
  {:vblank false
   :sprite-size :8x8
   :sprite-0-hit false
   :vram (vec (repeat 0x4000 0x00)) ; 16k VRAM
   :addr 0x0000
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

