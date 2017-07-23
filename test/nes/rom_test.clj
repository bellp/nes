(ns nes.rom-test
  (require [midje.sweet :refer :all]
           [nes.nestest :as nestest]
           [nes.rom :refer :all]
           [nes.debug :as debug]
           [nes.system :as sys]))

(fact "header contains NES preamble"
  (-> nestest/rom
      (read-header)
      (:preamble)) => "NES")

(fact "header contains number of PRG ROM 16 kb units"
  (-> nestest/rom
      (read-header)
      (:prg-rom-units)) => 1)

(fact "header contains number of CHR ROM 8 kb units"
  (-> nestest/rom
      (read-header)
      (:chr-rom-units)) => 1)

(fact "read-prg reads 16384 bytes"
  (-> nestest/rom
      (read-prg (read-header nestest/rom))
      (count)) => 16384)

(fact "write-into works"
  (write-into [0 1 2 3 4 5 6 7 8 9]
              4
              [42 43]) => [0 1 2 3 42 43 6 7 8 9])

(fact "run nestest"
  (let [prg-rom (read-prg nestest/rom (read-header nestest/rom))]
    (-> (sys/new-system)
        (update :mem (fn [m] (write-into m 0xC000 prg-rom)))
        (assoc :pc 0xC000)
        (sys/run 1000000)
        (debug/show-system)
        (get-in [:mem 0x02])) => 0x42))



