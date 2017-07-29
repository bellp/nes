(ns nes.status-test
  (:require [midje.sweet :refer :all]
            [nes.status :refer :all]
            [nes.system :refer :all]
            [nes.memory :as mem]))

(fact "get-status returns combined values of all 8 flags into one 8-bit value"
    (-> (new-system)
        (assoc :carry-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :carry-flag true)
        (get-status)) => 0x21

    (-> (new-system)
        (assoc :zero-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :zero-flag true)
        (get-status)) => 0x22

    (-> (new-system)
        (assoc :int-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :int-flag true)
        (get-status)) => 0x24

    (-> (new-system)
        (assoc :dec-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :dec-flag true)
        (get-status)) => 0x28

    (-> (new-system)
        (assoc :brk-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :brk-flag true)
        (get-status)) => 0x30

    (-> (new-system)
        (assoc :unused-flag false)
        (get-status)) => 0x00

    (-> (new-system)
        (assoc :unused-flag true)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :overflow-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :overflow-flag true)
        (get-status)) => 0x60

    (-> (new-system)
        (assoc :sign-flag false)
        (get-status)) => 0x20

    (-> (new-system)
        (assoc :sign-flag true)
        (get-status)) => 0xA0)

(fact "update-status updates all the flags given an 8-bit status value"
  (-> (new-system)
      (update-status 0x01)
      (:carry-flag)) => true

  (-> (new-system)
      (update-status 0x02)
      (:zero-flag)) => true

  (-> (new-system)
      (update-status 0x04)
      (:int-flag)) => true

  (-> (new-system)
      (update-status 0x08)
      (:dec-flag)) => true

  (-> (new-system)
      (update-status 0x10)
      (:brk-flag)) => false ; NOTE that PLP does not set brk flag

  (-> (new-system)
      (assoc :unused-flag false)
      (update-status 0x20)
      (:unused-flag)) => true

  (-> (new-system)
      (assoc :overflow-flag false)
      (update-status 0x40)
      (:overflow-flag)) => true

  (-> (new-system)
      (assoc :overflow-flag false)
      (update-status 0x80)
      (:sign-flag)) => true)


(fact "BRK pushes the PC onto the stack"
  (-> (new-system)
      (assoc :sp 0xFD)
      (assoc :pc 0x1234)
      (brk-opfn nil)
      (mem/read16 0x1FC)) => 0x1234)

(fact "BRK pushes the status register onto the stack"
  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :sign-flag true)
      (assoc :unused-flag false)
      (assoc :sp 0xFD)
      (brk-opfn nil)
      (mem/read8 0x1FB)) => 0x81)

(fact "BRK sets the brk flag"
  (-> (new-system)
      (brk-opfn nil)
      (:brk-flag)) => true)

(fact "BRK sets the PC to the address located at FFFE/FFFF"
  (-> (new-system)
      (mem/write16 0xFFFE 0x1234)
      (brk-opfn nil)
      (:pc)) => 0x1234)

(fact "RTI pulls the status register off the stack"
  (-> (new-system)
      (mem/push8 0xA1)
      (mem/push16 0x1234)
      (rti-opfn nil)
      (get-status)) => 0xA1)

(fact "RTI pulls the return address off the stack"
  (-> (new-system)
      (mem/push8 0xA1)
      (mem/push16 0x1234)
      (rti-opfn nil)
      (:pc)) => 0x1234)

(fact "RTI increments the SP by 3"
  (-> (new-system)
      (assoc :sp 0xFC)
      (rti-opfn nil)
      (:sp)) => 0xFF)

(fact "PLA pulls the top byte off the stack and places it in the accumulator"
  (-> (new-system)
      (mem/push8 0x3F)
      (pla-opfn nil)
      (:acc)) => 0x3F

  (-> (new-system)
      (mem/push16 0x1234)
      (pla-opfn nil)
      (:acc)) => 0x34)

(fact "PLA sets the zero flag iff the value pulled is zero"
  (-> (new-system)
      (mem/push8 0x00)
      (pla-opfn nil)
      (:zero-flag)) => true

  (-> (new-system)
      (mem/push8 0x3F)
      (pla-opfn nil)
      (:zero-flag)) => false)

(fact "PLA sets the sign flag iff the value pulled has bit 7 set"
  (-> (new-system)
      (mem/push8 0x80)
      (pla-opfn nil)
      (:sign-flag)) => true

  (-> (new-system)
      (mem/push8 0x3F)
      (pla-opfn nil)
      (:sign-flag)) => false)

(fact "PLP sets the flags from the value pulled off the stack"
  (-> (new-system)
      (mem/push8 0x81)
      (plp-opfn nil)
      (get-status)) => 0x81)
