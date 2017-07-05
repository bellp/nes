(ns nes.system-test
  (:require [midje.sweet :refer :all]
            [nes.debug :refer :all]
            [nes.assembly :refer :all]
            [nes.system :refer :all]))

(fact "end-to-end test"
  (-> (new-system)
      (compile-statement "ADC #$55")
      (assoc :pc 0x0000)
      (execute)
      (:acc)) => 0x55)
