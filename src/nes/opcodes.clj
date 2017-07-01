(ns nes.opcodes
  (:require [nes.arithmetic :refer :all]))

(def operand-sizes
  "The size of the operand (in bytes) for a given addressing mode"
    { :implied     0
      :accumulator 0
      :immediate   1
      :zeropage    1
      :zeropagex   1
      :absolute    2
      :absolutex   2
      :absolutey   2
      :indirect    1
      :indirectx   1
      :indirecty   1})

(def instruction-set
  { 0x00 { :name "BRK"
           :opcode 0x00
           :address-mode :implied
           :cycles 7}

    0x69 { :name "ADC"
           :opcode 0x69
           :function adc-opfn
           :address-mode :immediate
           :cycles 2}

    0x65 { :name "ADC"
           :opcode 0x65
           :function adc-opfn
           :address-mode :zeropage
           :cycles 3}

    0x75 { :name "ADC"
           :opcode 0x75
           :function adc-opfn
           :address-mode :zeropagex
           :cycles 4}

    0x6D { :name "ADC"
           :opcode 0x60
           :function adc-opfn
           :address-mode :absolute
           :cycles 4}

    0x70 { :name "ADC"
           :function adc-opfn
           :address-mode :absolutex
           :cycles 4}

    0x79 { :name "ADC"
           :opcode 0x79
           :function adc-opfn
           :address-mode :absolutey
           :cycles 4}

    0x61 { :name "ADC"
           :opcode 0x61
           :function adc-opfn
           :address-mode :indirectx
           :cycles 6}

    0x71 { :name "ADC"
           :opcode 0x71
           :function adc-opfn
           :address-mode :indirecty
           :cycles 5}

    0x4C { :name "JMP"
           :opcode 0x4C
           :function :todo
           :address-mode :absolute
           :cycles 3}

    0x6C { :name "JMP"
           :opcode 0x6C
           :function :todo
           :address-mode :indirect
           :cycles 5}

   0xF0 { :name "BEQ"
          :opcode 0xF0
          :address-mode :relative
          :cycles 2}})

