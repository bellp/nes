(ns nes.ppu-test
  (require [midje.sweet :refer :all]
           [nes.system :as sys]
           [nes.ppu :as ppu]))

(fact "Writing twice to $2006 sets the current VRAM address"
  (-> (sys/new-system)
      (ppu/write8 0x2006 0x12)
      (ppu/write8 0x2006 0x34)
      (get-in [:ppu :current-addr])) => 0x1234)

(fact "Reading from $2002 clears the PPU's address register"
  (let [system (ppu/write8 (sys/new-system) 0x2006 0x12)]))



