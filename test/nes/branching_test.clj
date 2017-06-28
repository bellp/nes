(ns nes.branching-test
    (:require [midje.sweet :refer :all]
              [nes.cpu :refer :all]
              [nes.system :refer :all]
              [nes.branching :refer :all]))

(fact "jmp changes PC to address"
  (-> (new-system)
      (assoc :pc 0x1234)
      (jmp 0x5678)
      (get :pc)) => 0x5678)

(fact "bcc only jumps when carry flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcc 0x5678)
      (get :pc)) => 0x5678

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcc 0x5678)
      (get :pc)) => 0x1234)

(fact "bcs only jumps when carry flag is set"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcs 0x5678)
      (get :pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcs 0x5678)
      (get :pc)) => 0x5678)



