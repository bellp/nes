(ns nes.ppu-test
  (require [midje.sweet :refer :all]
           [nes.system :as sys]
           [nes.ppu :as ppu]))

(fact "Writing to bits 0-1 of $2000 sets name table address"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:name-table-addr)) => 0x2000

  (-> (ppu/new)
      (ppu/write-2000 0x01)
      (:name-table-addr)) => 0x2400

  (-> (ppu/new)
      (ppu/write-2000 0x02)
      (:name-table-addr)) => 0x2800

  (-> (ppu/new)
      (ppu/write-2000 0x03)
      (:name-table-addr)) => 0x2C00)

(fact "Writing to bit 2 of $2000 sets addr-inc-by to either 1 or 32"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:addr-inc-by)) => 1

  (-> (ppu/new)
      (ppu/write-2000 0x04)
      (:addr-inc-by)) => 32)

(fact "Writing to bit 3 of $2000 determines which pattern table sprites are stored in ($0000 for 0, or $10000 for 1)"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:sprite-pattern-table)) => 0x0000

  (-> (ppu/new)
      (ppu/write-2000 0x08)
      (:sprite-pattern-table)) => 0x1000)

(fact "Writing to bit 4 of $2000 determines which pattern table the background is stored in ($0000 for 0, or $10000 for 1)"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:background-pattern-table)) => 0x0000

  (-> (ppu/new)
      (ppu/write-2000 0x10)
      (:background-pattern-table)) => 0x1000)

(fact "Writing to bit 5 of $2000 determines the size of sprites (8x8 if 0, 8x16 if 1)"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:sprite-size)) => :8x8

  (-> (ppu/new)
      (ppu/write-2000 0x20)
      (:sprite-size)) => :8x16)

(fact "Writing to bit 7 of $2000 determines if a NMI should occur upon V-Blank"
  (-> (ppu/new)
      (ppu/write-2000 0x00)
      (:vblank)) => false

  (-> (ppu/new)
      (ppu/write-2000 0x80)
      (:vblank)) => true)

(fact "Writing to bit 0 of $2001 deterines whether PPU is in color (0) or monochrome (1) mode"
  (-> (ppu/new)
      (ppu/write-2001 0x00)
      (:monochrome)) => false

  (-> (ppu/new)
      (ppu/write-2001 0x01)
      (:monochrome)) => true)

