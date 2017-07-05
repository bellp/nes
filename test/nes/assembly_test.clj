(ns nes.assembly-test
    (:use [midje.sweet]
          [nes.system]
          [nes.assembly]))

(fact "get-instruction-name correctly returns the 3 letter name if the statement instruction is correct"
  (get-instruction-name "LDA #$02") => "LDA"
  (get-instruction-name "BRK") => "BRK"
  (get-instruction-name " INX  ") => "INX")


(fact "valid-instruction-name? will check if an instruction is valid"
  (valid-instruction-name? "BRK") => true
  (valid-instruction-name? "ADC") => true
  (valid-instruction-name? "ZZZ") => false
  (valid-instruction-name? "BR") => false)

(fact "find-arg-match describes immediate addressing modes"
  (describe-instruction "ADC #$10") => {:name "ADC"
                                        :address-mode :immediate
                                        :opcode 0x69
                                        :operand 0x10})

(fact "find-arg-match returns absolute addressing mode"
  (describe-instruction "ADC $1000") => {:name "ADC"
                                         :address-mode :absolute
                                         :opcode 0x6D
                                         :operand 0x1000})

(fact "find-arg-match returns absolutex addressing mode"
  (describe-instruction "ADC $1000,X") => {:name "ADC"
                                           :address-mode :absolutex
                                           :opcode 0x7D
                                           :operand 0x1000})

(fact "find-arg-match returns absolutey addressing mode"
  (describe-instruction "ADC $1000,Y") => {:name "ADC"
                                           :address-mode :absolutey
                                           :opcode 0x79
                                           :operand 0x1000})


(fact "find-arg-match returns zeropage addressing mode"
  (describe-instruction "ADC $10") => {:name "ADC"
                                       :address-mode :zeropage
                                       :opcode 0x65
                                       :operand 0x10})

(fact "find-arg-match returns zeropagex addressing mode"
  (describe-instruction "ADC $10,X") => {:name "ADC"
                                         :address-mode :zeropagex
                                         :opcode 0x75
                                         :operand 0x10})

(fact "find-arg-match returns zeropagey addressing mode"
  (describe-instruction "LDX $10,Y") => {:name "LDX"
                                         :address-mode :zeropagey
                                         :opcode 0xB6
                                         :operand 0x10})

(fact "find-arg-match returns indirect addressing mode"
  (describe-instruction "JMP ($1000)") => {:name "JMP"
                                           :address-mode :indirect
                                           :opcode 0x6C
                                           :operand 0x1000})

(fact "find-arg-match returns indirectx addressing mode"
  (describe-instruction "ADC ($10,X)") => {:name "ADC"
                                           :address-mode :indirectx
                                           :opcode 0x61
                                           :operand 0x10})

(fact "find-arg-match returns indirecty addressing mode"
  (describe-instruction "ADC ($10),Y") => {:name "ADC"
                                           :address-mode :indirecty
                                           :opcode 0x71
                                           :operand 0x10})

(fact "find-arg-match returns relative addressing mode"
  (describe-instruction "BEQ $10") => {:name "BEQ"
                                       :address-mode :relative
                                       :opcode 0xF0
                                       :operand 0x10})

(fact "find-arg-match returns implied addressing mode"
  (describe-instruction "INX") => {:name "INX"
                                   :address-mode :implied
                                   :opcode 0xE8
                                   :operand nil})

(fact "find-arg-match returns accumulator addressing mode"
  (describe-instruction "LSR A") => {:name "LSR"
                                     :address-mode :accumulator
                                     :opcode 0x4A
                                     :operand nil})

(fact "compile-statement writes opcode for an instruction with a 2-byte operand"
  (-> (new-system)
      (compile-statement "ADC $3344")
      (assoc :pc 0x0000)
      (get-current-instruction)
      (:opcode)) => 0x6D)

(fact "compile-statement writes operand for an instruction with a 2-byte operand"
  (-> (new-system)
      (compile-statement "ADC $3344")
      (assoc :pc 0x0000)
      (get-current-instruction)
      (:operand)) => 0x3344)

(fact "compile-statement updates PC after compiling instruction with a 2-byte operand "
  (-> (new-system)
      (compile-statement "ADC $3344")
      (:pc)) => 0x0003)

(fact "compile-statement writes opcode for an instruction with a 1-byte operand"
  (-> (new-system)
      (compile-statement "ADC $33")
      (assoc :pc 0x0000)
      (get-current-instruction)
      (:opcode)) => 0x65)

(fact "compile-statement writes operand for an instruction with a 1-byte operand"
  (-> (new-system)
      (compile-statement "ADC $33")
      (assoc :pc 0x0000)
      (get-current-instruction)
      (:operand)) => 0x33)

(fact "compile-statement updates PC after compiling instruction with a 1-byte operand "
  (-> (new-system)
      (compile-statement "ADC $33")
      (:pc)) => 0x0002)

(fact "compile-statement writes opcode for an instruction with no operand"
  (-> (new-system)
      (compile-statement "INX")
      (assoc :pc 0x0000)
      (get-current-instruction)
      (:opcode)) => 0xE8)

(fact "compile-statement updates PC after compiling instruction with no operand "
  (-> (new-system)
      (compile-statement "INX")
      (:pc)) => 0x0001)



