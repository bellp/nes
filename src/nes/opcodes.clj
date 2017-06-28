(ns nes.opcodes
  (:require [nes.arithmetic :refer :all]))

(def operand-size
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
      :indirecty   1 })

(def instructions
  { 0x00 { :name "BRK"
           :address-mode :implied
           :cycles 7 }

    0x69 { :name "ADC"
           :function adc
           :address-mode :immediate
           :cycles 2 }

    0x65 { :name "ADC"
           :function adc
           :address-mode :zeropage
           :cycles 3 }

    0x75 { :name "ADC"
           :function adc
           :address-mode :zeropagex
           :cycles 4 }

    0x60 { :name "ADC"
           :function adc
           :address-mode :absolute
           :cycles 4 }

    0x70 { :name "ADC"
           :function adc
           :address-mode :absolutex
           :cycles 4 }

    0x79 { :name "ADC"
           :function adc
           :address-mode :absolutey
           :cycles 4 }

    0x61 { :name "ADC"
           :function adc
           :address-mode :indirectx
           :cycles 6 }

    0x71 { :name "ADC"
           :function adc
           :address-mode :indirecty
           :cycles 5 }

    0x4C { :name "JMP"
           :function :todo
           :address-mode :absolute
           :cycles 3 }

    0x6C { :name "JMP"
           :function :todo
           :address-mode :indirect
           :cycles 5 }

   0xF0 { :name "BEQ"
          :address-mode :relative
          :cycles 2 }
  })
