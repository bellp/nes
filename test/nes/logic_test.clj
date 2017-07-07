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
