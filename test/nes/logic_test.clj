(ns nes.logic-test
  (:require [midje.sweet :refer :all]
            [nes.logic :refer :all]
            [nes.system :refer :all]))

(fact "AND executes against accumulator and m"
  (-> (new-system)
      (assoc :acc 0x03)
      (and-opfn 0x01)
      (:acc)) => 0x01)

(fact "AND sets zero flag only when result is zero"
  (-> (new-system)
      (assoc :acc 0x03)
      (and-opfn 0x00)
      (:zero-flag)) => true

  (-> (new-system)
      (assoc :acc 0x03)
      (and-opfn 0x01)
      (:zero-flag)) => false)

(fact "AND sets sign flag only when result is negative (bit 7 is set)"
  (-> (new-system)
      (assoc :acc 0xF0)
      (and-opfn 0x80)
      (:sign-flag)) => true

  (-> (new-system)
      (assoc :acc 0xFF)
      (and-opfn 0x7F)
      (:sign-flag)) => false)

(fact "ORA executes against accumulator and m"
  (-> (new-system)
      (assoc :acc 0x02)
      (ora-opfn 0x01)
      (:acc)) => 0x03)

(fact "ORA sets zero flag only when result is zero"
  (-> (new-system)
      (assoc :acc 0x00)
      (ora-opfn 0x00)
      (:zero-flag)) => true

  (-> (new-system)
      (assoc :acc 0x01)
      (ora-opfn 0x00)
      (:zero-flag)) => false)

(fact "ORA sets sign flag only when result is negative (bit 7 is set)"
  (-> (new-system)
      (assoc :acc 0x7F)
      (ora-opfn 0x80)
      (:sign-flag)) => true

  (-> (new-system)
      (assoc :acc 0x7F)
      (ora-opfn 0x00)
      (:sign-flag)) => false)

(fact "EOR executes against accumulator and m"
  (-> (new-system)
      (assoc :acc 0x03)
      (eor-opfn 0x01)
      (:acc)) => 0x02)

(fact "EOR sets zero flag only when result is zero"
  (-> (new-system)
      (assoc :acc 0x03)
      (eor-opfn 0x03)
      (:zero-flag)) => true

  (-> (new-system)
      (assoc :acc 0x01)
      (eor-opfn 0x00)
      (:zero-flag)) => false)

(fact "EOR sets sign flag only when result is negative (bit 7 is set)"
  (-> (new-system)
      (assoc :acc 0x80)
      (eor-opfn 0x70)
      (:sign-flag)) => true

  (-> (new-system)
      (assoc :acc 0x80)
      (eor-opfn 0x80)
      (:sign-flag)) => false)

(fact "BIT sets the zero flag if the result of ANDing A and M are zero"
  (-> (new-system)
      (assoc :acc 0x00)
      (bit-opfn 0xFF)
      (:zero-flag)) => true)

(fact "BIT sets the sign flag if bit 7 of m is set"
  (-> (new-system)
      (bit-opfn 0x80)
      (:sign-flag)) => true

  (-> (new-system)
      (bit-opfn 0x7F)
      (:sign-flag)) => false)

(fact "BIT sets the overflow flag if bit 6 of m is set"
  (-> (new-system)
      (bit-opfn 0x40)
      (:overflow-flag)) => true

  (-> (new-system)
      (bit-opfn 0x3F)
      (:overflow-flag)) => false)

(fact "ASL shifts bits in accumulator left once"
  (-> (new-system)
      (assoc :acc 0x04)
      (asl-opfn :accumulator)
      (get :acc)) => 0x08)

(fact "ASL sets carry flag to old bit 7"
  (-> (new-system)
      (assoc :acc 0x80)
      (asl-opfn :accumulator)
      (get :carry-flag)) => true

  (-> (new-system)
      (assoc :acc 0x80)
      (asl-opfn :accumulator)
      (get :carry-flag)) => true)

(fact "ASL sets zero flag if result is 0"
  (-> (new-system)
      (assoc :acc 0x80)
      (asl-opfn :accumulator)
      (get :zero-flag)) => true

  (-> (new-system)
      (assoc :acc 0x40)
      (asl-opfn :accumulator)
      (get :zero-flag)) => false)

(fact "ASL sets sign flag if result has bit 7 set,"
  (-> (new-system)
      (assoc :acc 0x40)
      (asl-opfn :accumulator)
      (get :sign-flag)) => true

  (-> (new-system)
      (assoc :acc 0x80)
      (asl-opfn :accumulator)
      (get :sign-flag)) => false)

(fact "LSR shifts bits in accumulator right once"
  (-> (new-system)
      (assoc :acc 0x08)
      (lsr-opfn :accumulator)
      (get :acc)) => 0x04)

(fact "LSR sets carry flag to old bit 0"
  (-> (new-system)
      (assoc :acc 0x01)
      (lsr-opfn :accumulator)
      (get :carry-flag)) => true

  (-> (new-system)
      (assoc :acc 0x02)
      (lsr-opfn :accumulator)
      (get :carry-flag)) => false)

(fact "LSR sets zero flag if result is 0"
  (-> (new-system)
      (assoc :acc 0x01)
      (lsr-opfn :accumulator)
      (get :zero-flag)) => true

  (-> (new-system)
      (assoc :acc 0x02)
      (lsr-opfn :accumulator)
      (get :zero-flag)) => false)

(fact "LSR always sets sign flag to zero"
  (-> (new-system)
      (assoc :acc 0x80)
      (lsr-opfn :accumulator)
      (get :sign-flag)) => false

  (-> (new-system)
      (assoc :acc 0x00)
      (lsr-opfn :accumulator)
      (get :sign-flag)) => false)

(fact "ROL shifts bits in accumulator left once"
  (-> (new-system)
      (assoc :acc 0x04)
      (rol-opfn :accumulator)
      (get :acc)) => 0x08)

(fact "ROL sets bit 0 to the old carry flag"
  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :acc 0x01)
      (rol-opfn :accumulator)
      (get :acc)) => 0x03

  (-> (new-system)
      (assoc :carry-flag false)
      (assoc :acc 0x01)
      (rol-opfn :accumulator)
      (get :acc)) => 0x02)

(fact "ROL sets sets the carry flag to the old bit 7"
  (-> (new-system)
      (assoc :acc 0x80)
      (rol-opfn :accumulator)
      (get :carry-flag)) => true

  (-> (new-system)
      (assoc :acc 0x40)
      (rol-opfn :accumulator)
      (get :carry-flag)) => false)

(fact "ROL sets zero flag if result if zero"
  (-> (new-system)
      (assoc :carry-flag false)
      (assoc :acc 0x00)
      (rol-opfn :accumulator)
      (get :zero-flag)) => true

  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :acc 0x00)
      (rol-opfn :accumulator)
      (get :zero-flag)) => false)

(fact "ROL sets sign flag if result has bit 7 set"
  (-> (new-system)
      (assoc :acc 0x40)
      (rol-opfn :accumulator)
      (get :sign-flag)) => true

  (-> (new-system)
      (assoc :acc 0x20)
      (rol-opfn :accumulator)
      (get :sign-flag)) => false)

(fact "ROR shifts bits in accumulator right once"
  (-> (new-system)
      (assoc :acc 0x04)
      (ror-opfn :accumulator)
      (get :acc)) => 0x02)

(fact "ROR sets bit 7 to the old carry flag"
  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :acc)) => 0x80

  (-> (new-system)
      (assoc :carry-flag false)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :acc)) => 0x00)

(fact "ROR sets sets the carry flag to the old bit 0"
  (-> (new-system)
      (assoc :acc 0x01)
      (ror-opfn :accumulator)
      (get :carry-flag)) => true

  (-> (new-system)
      (assoc :acc 0x40)
      (ror-opfn :accumulator)
      (get :carry-flag)) => false)

(fact "ROR sets zero flag if result if zero"
  (-> (new-system)
      (assoc :carry-flag false)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :zero-flag)) => true

  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :zero-flag)) => false)

(fact "ROR sets sign flag if result has bit 7 set"
  (-> (new-system)
      (assoc :carry-flag true)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :sign-flag)) => true

  (-> (new-system)
      (assoc :carry-flag false)
      (assoc :acc 0x00)
      (ror-opfn :accumulator)
      (get :sign-flag)) => false)
