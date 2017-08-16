(ns nes.mapper-test
  (:require [midje.sweet :refer :all]
            [nes.system :refer :all]
            [nes.mapper :refer :all]
            [nes.memory :as mem]))

(fact "memory addresses 0x000 to 0x7FF mirror up to 0x1FFF"
  (-> (new-system)
      (write-mem8 0x0000 0x33)
      (read-mem8 0x800)
      (get 0)) => 0x33

  (-> (new-system)
      (write-mem8 0x07FF 0xFF)
      (read-mem8 0xFFF)
      (get 0)) => 0xFF

  (-> (new-system)
      (write-mem8 0x07FF 0xFF)
      (read-mem8 0x1FFF)
      (get 0)) => 0xFF)

(defn- test-system
  []
  (-> (new-system)
      (assoc :mapper
              {:mem (vec (repeat 0x800 0x00))
               :read-fn mapper0-read8
               :write-fn mapper0-write8})))

(fact "mapper0 write8/read8"
  (-> (test-system)
      (mem/write8 0x1FC 0x33)
      (mem/read8 0x1FC)
      (get 0)) => 0x33)

(fact "mapper0 write16/read16"
  (-> (test-system)
      (mem/write16 0x01FC 0x1234)
      (mem/read16 0x01FC)) => 0x1234)
