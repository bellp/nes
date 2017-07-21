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
