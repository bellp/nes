(ns nes.branching-test
    (:require [midje.sweet :refer :all]
              [nes.system :refer :all]
              [nes.branching :refer :all]))

(fact "jmp changes PC to address"
  (-> (new-system)
      (assoc-in [:cpu :pc] 0x1234)
      (jmp 0x5678)
      (get-in [:cpu :pc])) => 0x5678)
