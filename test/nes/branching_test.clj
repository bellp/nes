(ns nes.branching-test
    (:require [midje.sweet :refer :all]
              [nes.cpu :refer :all]
              [nes.system :refer :all]
              [nes.branching :refer :all]))

(fact "jmp changes PC to address"
  (-> (new-system)
      (assoc :pc 0x1234)
      (jmp-opfn 0x5678)
      (get :pc)) => 0x5678)

(fact "bcc only branches when carry flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcc-opfn 0x5678)
      (get :pc)) => 0x5678

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcc-opfn 0x5678)
      (get :pc)) => 0x1234)

(fact "bcc increments clock by 1 if branch succeeds"
  (-> (new-system)
      (assoc :carry-flag false)
      (bcc-opfn 0x0200)
      (get :cycle-count)) => 1)

(fact "bcs only branches when carry flag is set"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcs-opfn 0x5678)
      (get :pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcs-opfn 0x5678)
      (get :pc)) => 0x5678)

(fact "bcs increments clock by 1 if branch succeeds"
  (-> (new-system)
      (assoc :carry-flag true)
      (bcs-opfn 0x0200)
      (get :cycle-count)) => 1)
