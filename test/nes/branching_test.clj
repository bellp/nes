(ns nes.branching-test
    (:require [midje.sweet :refer :all]
              [nes.system :refer :all]
              [nes.branching :refer :all]))

(fact "branch can jump with positive relative number"
  (-> (new-system)
      (assoc :pc 0x1000)
      (branch 0x20)
      (:pc)) => 0x1020)

(fact "branch can jump with negative relative number"
  (-> (new-system)
      (assoc :pc 0x1000)
      (branch 0xE0)
      (:pc)) => 0x0FE0)

(fact "branch increments the cycle count by one"
    (-> (new-system)
        (branch 0x20)
        (:cycle-count)) => 1)

(fact "JMP changes PC to address"
  (-> (new-system)
      (assoc :pc 0x1234)
      (jmp-opfn 0x5678)
      (:pc)) => 0x5678)

(fact "BCC only branches when carry flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcc-opfn 0x01)
      (:pc)) => 0x1235

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcc-opfn 0x01)
      (:pc)) => 0x1234)

(fact "BCS only branches when carry flag is set"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag false)
      (bcs-opfn 0x02)
      (:pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :carry-flag true)
      (bcs-opfn 0x05)
      (:pc)) => 0x1239)

(fact "BEQ only branches when zero flag is set"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :zero-flag false)
      (beq-opfn 0x02)
      (:pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :zero-flag true)
      (beq-opfn 0x05)
      (:pc)) => 0x1239)

(fact "BNE only branches when zero flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :zero-flag false)
      (bne-opfn 0x02)
      (:pc)) => 0x1236

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :zero-flag true)
      (bne-opfn 0x05)
      (:pc)) => 0x1234)

(fact "BMI only branches when sign flag is set"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :sign-flag false)
      (bmi-opfn 0x02)
      (:pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :sign-flag true)
      (bmi-opfn 0x05)
      (:pc)) => 0x1239)

(fact "BPL only branches when sign flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :sign-flag false)
      (bpl-opfn 0x02)
      (:pc)) => 0x1236

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :sign-flag true)
      (bpl-opfn 0x05)
      (:pc)) => 0x1234)

(fact "BVC only branches when overflow flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :overflow-flag false)
      (bvc-opfn 0x02)
      (:pc)) => 0x1236

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :overflow-flag true)
      (bvc-opfn 0x05)
      (:pc)) => 0x1234)

(fact "BVS only branches when overflow flag is clear"
  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :overflow-flag false)
      (bvs-opfn 0x02)
      (:pc)) => 0x1234

  (-> (new-system)
      (assoc :pc 0x1234)
      (assoc :overflow-flag true)
      (bvs-opfn 0x05)
      (:pc)) => 0x1239)
