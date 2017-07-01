(ns nes.system-test
  (:require [midje.sweet :refer :all]
            [nes.debug :refer :all]
            [nes.system :refer :all]))

(fact "end-to-end test"
  (-> (new-system)
      (assoc :x 0x01)
      (assoc-in [:mem 0x00] 0x6D)
      (assoc-in [:mem 0x01] 0xCD)
      (assoc-in [:mem 0x02] 0xAB)
      (assoc-in [:mem 0xABCD] 0x33)
      (execute)
      (show-system)) => nil)
