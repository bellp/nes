(ns nes.transfer-test
  (:require [midje.sweet :refer :all]
            [nes.system :refer :all]
            [nes.transfer :refer :all]))

(fact "load-register loads a byte of memory into the accumulator"
  (-> (new-system)
      (load-register 0x3F :acc)
      (:acc)) => 0x3F)

(fact "load-register sets the zero flag iff the value is zero"
  (-> (new-system)
      (load-register 0x3F :acc)
      (:zero-flag)) => false

  (-> (new-system)
      (load-register 0x00 :acc)
      (:zero-flag)) => true)

(fact "load-register sets the sign flag iff the value has bit 7 set"
  (-> (new-system)
      (load-register 0x3F :acc)
      (:sign-flag)) => false

  (-> (new-system)
      (load-register 0x80 :acc)
      (:sign-flag)) => true)

(fact "store-register stores a register's value in memory"
  (-> (new-system)
      (assoc :acc 0x3F)
      (store-register :acc 0x1234)
      (get-in [:mem 0x1234])) => 0x3F)