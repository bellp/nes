(ns nes.system-test
  (:require [midje.sweet :refer :all]
            [nes.debug :refer :all]
            [nes.system :refer :all]))

(fact "end-to-end test"
  (-> (new-system)
      (assoc-in [:mem 0x00] 0x75)
      (assoc-in [:mem 0x01] 0x60)
      (assoc-in [:mem 0x60] 0x11)
      (execute)
      (show-system)) => nil)
