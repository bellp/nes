(ns nes.opcodes
  (:require [nes.arithmetic :refer :all]
            [nes.logic :as logic]
            [nes.branching :as branch]
            [nes.status :as status]
            [nes.transfer :as xfer]))

(def operand-sizes
  "The size of the operand (in bytes) for a given addressing mode"
    { :implied     0
      :accumulator 0
      :relative    1
      :immediate   1
      :zeropage    1
      :zeropagex   1
      :zeropagey   1
      :absolute    2
      :absolutex   2
      :absolutey   2
      :indirect    2
      :indirectx   1
      :indirecty   1})

(def instructions
  {"ADC" {:function adc-opfn
          :mutates-memory false
          :codes [{:opcode 0x69
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0x65
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x75
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x6D
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x7D
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x79
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0x61
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0x71
                   :address-mode :indirecty
                   :cycles 5}]}

   "AND" {:function logic/and-opfn
          :mutates-memory false
          :codes [{:opcode 0x29
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0x25
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x35
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x2D
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x3D
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x39
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0x21
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0x31
                   :address-mode :indirecty
                   :cycles 5}]}

   "ASL" {:function logic/asl-opfn
          :mutates-memory true
          :codes [{:opcode 0x0A
                   :address-mode :accumulator
                   :cycles 2}

                  {:opcode 0x06
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0x16
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0x0E
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0x1E
                   :address-mode :absolutex
                   :cycles 7}]}

   "BCC" {:function branch/bcc-opfn
          :mutates-memory false
          :codes [{:opcode 0x90
                   :address-mode :relative
                   :cycles 2}]}

   "BCS" {:function branch/bcs-opfn
          :mutates-memory false
          :codes [{:opcode 0xB0
                   :address-mode :relative
                   :cycles 2}]}

   "BEQ" {:function branch/beq-opfn
          :mutates-memory false
          :codes [{:opcode 0xF0
                   :address-mode :relative
                   :cycles 2}]}

   "BIT" {:function logic/bit-opfn
          :mutates-memory false
          :codes [{:opcode 0x24
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x2C
                   :address-mode :absolute
                   :cycles 4}]}

   "BMI" {:function branch/bmi-opfn
          :mutates-memory false
          :codes [{:opcode 0x30
                   :address-mode :relative
                   :cycles 2}]}

   "BNE" {:function branch/bne-opfn
          :mutates-memory false
          :codes [{:opcode 0xD0
                   :address-mode :relative
                   :cycles 2}]}

   "BPL" {:function branch/bpl-opfn
          :mutates-memory false
          :codes [{:opcode 0x10
                   :address-mode :relative
                   :cycles 2}]}

   "BRK" {:function status/brk-opfn
          :mutates-memory false
          :codes [{:opcode 0x00
                   :address-mode :implied
                   :cycles 7}]}

   "BVC" {:function branch/bvc-opfn
          :mutates-memory false
          :codes [{:opcode 0x50
                   :address-mode :relative
                   :cycles 2}]}

   "BVS" {:function branch/bvs-opfn
          :mutates-memory false
          :codes [{:opcode 0x70
                   :address-mode :relative
                   :cycles 2}]}

   "CLC" {:function status/clc-opfn
          :mutates-memory false
          :codes [{:opcode 0x18
                   :address-mode :implied
                   :cycles 2}]}

   "CLD" {:function status/cld-opfn
          :mutates-memory false
          :codes [{:opcode 0xD8
                   :address-mode :implied
                   :cycles 2}]}

   "CLI" {:function status/cli-opfn
          :mutates-memory false
          :codes [{:opcode 0x58
                   :address-mode :implied
                   :cycles 2}]}

   "CLV" {:function status/clv-opfn
          :mutates-memory false
          :codes [{:opcode 0xB8
                   :address-mode :implied
                   :cycles 2}]}

   "CMP" {:function cmp-opfn
          :mutates-memory false
          :codes [{:opcode 0xC9
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xC5
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xD5
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0xCD
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0xDD
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0xD9
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0xC1
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0xD1
                   :address-mode :indirecty
                   :cycles 5}]}

   "CPX" {:function cpx-opfn
          :mutates-memory false
          :codes [{:opcode 0xE0
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xE4
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xEC
                   :address-mode :absolute
                   :cycles 4}]}

   "CPY" {:function cpy-opfn
          :mutates-memory false
          :codes [{:opcode 0xC0
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xC4
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xCC
                   :address-mode :absolute
                   :cycles 4}]}

   "DEC" {:function dec-opfn
          :mutates-memory true
          :codes [{:opcode 0xC6
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0xD6
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0xCE
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0xDE
                   :address-mode :absolutex
                   :cycles 7}]}

   "DEX" {:function dex-opfn
          :mutates-memory false
          :codes [{:opcode 0xCA
                   :address-mode :implied
                   :cycles 2}]}

   "DEY" {:function dey-opfn
          :mutates-memory false
          :codes [{:opcode 0x88
                   :address-mode :implied
                   :cycles 2}]}

   "EOR" {:function logic/eor-opfn
          :mutates-memory false
          :codes [{:opcode 0x49
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0x45
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x55
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x4D
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x5D
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x59
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0x41
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0x51
                   :address-mode :indirecty
                   :cycles 5}]}

   "INC" {:function inc-opfn
          :mutates-memory true
          :codes [{:opcode 0xE6
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0xF6
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0xEE
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0xFE
                   :address-mode :absolutex
                   :cycles 7}]}

   "INX" {:function inx-opfn
          :mutates-memory false
          :codes [{:opcode 0xE8
                   :address-mode :implied
                   :cycles 2}]}

   "INY" {:function iny-opfn
          :mutates-memory false
          :codes [{:opcode 0xC8
                   :address-mode :implied
                   :cycles 2}]}

   "JMP" {:function branch/jmp-opfn
          :mutates-memory true
          :codes [{:opcode 0x4C
                   :address-mode :absolute
                   :cycles 3}

                  {:opcode 0x6C
                   :address-mode :indirect
                   :cycles 5}]}

   "JSR" {:function branch/jsr-opfn
          :mutates-memory true
          :codes [{:opcode 0x20
                   :address-mode :absolute
                   :cycles 6}]}

   "LDA" {:function xfer/lda-opfn
          :mutates-memory false
          :codes [{:opcode 0xA9
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xA5
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xB5
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0xAD
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0xBD
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0xB9
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0xA1
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0xB1
                   :address-mode :indirecty
                   :cycles 5}]}

   "LDX" {:function xfer/ldx-opfn
          :mutates-memory false
          :codes [{:opcode 0xA2
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xA6
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xB6
                   :address-mode :zeropagey
                   :cycles 4}

                  {:opcode 0xAE
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0xBE
                   :address-mode :absolutey
                   :cycles 4}]}

   "LDY" {:function xfer/ldy-opfn
          :mutates-memory false
          :codes [{:opcode 0xA0
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xA4
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xB4
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0xAC
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0xBC
                   :address-mode :absolutex
                   :cycles 4}]}

   "LSR" {:function logic/lsr-opfn
          :mutates-memory true
          :codes [{:opcode 0x4A
                   :address-mode :accumulator
                   :cycles 2}

                  {:opcode 0x46
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0x56
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0x4E
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0x5E
                   :address-mode :absolutex
                   :cycles 7}]}

   "NOP" {:function status/nop-opfn
          :mutates-memory false
          :codes [{:opcode 0xEA
                   :address-mode :implied
                   :cycles 2}]}

   "ORA" {:function logic/ora-opfn
          :mutates-memory false
          :codes [{:opcode 0x09
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0x05
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x15
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x0D
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x1D
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x19
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0x01
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0x11
                   :address-mode :indirecty
                   :cycles 5}]}

   "PHA" {:function status/pha-opfn
          :mutates-memory false
          :codes [{:opcode 0x48
                   :address-mode :implied
                   :cycles 3}]}

   "PHP" {:function status/php-opfn
          :mutates-memory false
          :codes [{:opcode 0x08
                   :address-mode :implied
                   :cycles 3}]}

   "PLA" {:function status/pla-opfn
          :mutates-memory false
          :codes [{:opcode 0x68
                   :address-mode :implied
                   :cycles 4}]}

   "PLP" {:function status/plp-opfn
          :mutates-memory false
          :codes [{:opcode 0x28
                   :address-mode :implied
                   :cycles 4}]}

   "ROL" {:function logic/rol-opfn
          :mutates-memory true
          :codes [{:opcode 0x2A
                   :address-mode :accumulator
                   :cycles 2}

                  {:opcode 0x26
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0x36
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0x2E
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0x3E
                   :address-mode :absolutex
                   :cycles 7}]}

   "ROR" {:function logic/ror-opfn
          :mutates-memory true
          :codes [{:opcode 0x6A
                   :address-mode :accumulator
                   :cycles 2}

                  {:opcode 0x66
                   :address-mode :zeropage
                   :cycles 5}

                  {:opcode 0x76
                   :address-mode :zeropagex
                   :cycles 6}

                  {:opcode 0x6E
                   :address-mode :absolute
                   :cycles 6}

                  {:opcode 0x7E
                   :address-mode :absolutex
                   :cycles 7}]}

   "RTI" {:function status/rti-opfn
          :mutates-memory false
          :codes [{:opcode 0x40
                   :address-mode :implied
                   :cycles 6}]}

   "RTS" {:function branch/rts-opfn
          :mutates-memory false
          :codes [{:opcode 0x60
                   :address-mode :implied
                   :cycles 6}]}

   "SBC" {:function sbc-opfn
          :mutates-memory false
          :codes [{:opcode 0xE9
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0xE5
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0xF5
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0xED
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0xFD
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0xF9
                   :address-mode :absolutey
                   :cycles 4}

                  {:opcode 0xE1
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0xF1
                   :address-mode :indirecty
                   :cycles 5}]}

   "SEC" {:function status/sec-opfn
          :mutates-memory false
          :codes [{:opcode 0x38
                   :address-mode :implied
                   :cycles 2}]}

   "SED" {:function status/sed-opfn
          :mutates-memory false
          :codes [{:opcode 0xF8
                   :address-mode :implied
                   :cycles 2}]}

   "SEI" {:function status/sei-opfn
          :mutates-memory false
          :codes [{:opcode 0x78
                   :address-mode :implied
                   :cycles 2}]}

   "STA" {:function xfer/sta-opfn
          :mutates-memory true
          :codes [{:opcode 0x85
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x95
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x8D
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x9D
                   :address-mode :absolutex
                   :cycles 5}

                  {:opcode 0x99
                   :address-mode :absolutey
                   :cycles 5}

                  {:opcode 0x81
                   :address-mode :indirectx
                   :cycles 6}

                  {:opcode 0x91
                   :address-mode :indirecty
                   :cycles 6}]}

   "STX" {:function xfer/stx-opfn
          :mutates-memory true
          :codes [{:opcode 0x86
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x96
                   :address-mode :zeropagey
                   :cycles 4}

                  {:opcode 0x8E
                   :address-mode :absolute
                   :cycles 4}]}

   "STY" {:function xfer/sty-opfn
          :mutates-memory true
          :codes [{:opcode 0x84
                   :address-mode :zeropage
                   :cycles 3}

                  {:opcode 0x94
                   :address-mode :zeropagex
                   :cycles 4}

                  {:opcode 0x8C
                   :address-mode :absolute
                   :cycles 4}]}

   "TAX" {:function xfer/tax-opfn
          :mutates-memory false
          :codes [{:opcode 0xAA
                   :address-mode :implied
                   :cycles 2}]}

   "TAY" {:function xfer/tay-opfn
          :mutates-memory false
          :codes [{:opcode 0xA8
                   :address-mode :implied
                   :cycles 2}]}

   "TSX" {:function xfer/tsx-opfn
          :mutates-memory false
          :codes [{:opcode 0xBA
                   :address-mode :implied
                   :cycles 2}]}

   "TXA" {:function xfer/txa-opfn
          :mutates-memory false
          :codes [{:opcode 0x8A
                   :address-mode :implied
                   :cycles 2}]}

   "TXS" {:function xfer/txs-opfn
          :mutates-memory false
          :codes [{:opcode 0x9A
                   :address-mode :implied
                   :cycles 2}]}

   "TYA" {:function xfer/tya-opfn
          :mutates-memory false
          :codes [{:opcode 0x98
                   :address-mode :implied
                   :cycles 2}]}

   ; Undocumented.
   "DOP" {:function status/nop-opfn
          :mutates-memory false
          :codes [{:opcode 0x04
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0x14
                   :address-mode :immediate
                   :cycles 4}

                  {:opcode 0x34
                   :address-mode :immediate
                   :cycles 4}

                  {:opcode 0x44
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0x54
                   :address-mode :immediate
                   :cycles 4}

                  {:opcode 0x64
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0x74
                   :address-mode :immediate
                   :cycles 4}

                  {:opcode 0x80
                   :address-mode :immediate
                   :cycles 2}

                  {:opcode 0x82
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0x89
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0xC2
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0xD4
                   :address-mode :immediate
                   :cycles 4}

                  {:opcode 0xE2
                   :address-mode :immediate
                   :cycles 3}

                  {:opcode 0xF4
                   :address-mode :immediate
                   :cycles 4}]}

   ; Undocumented. aka *NOP
   "SOP" {:function status/nop-opfn
          :mutates-memory false
          :codes [{:opcode 0x1A
                   :address-mode :implied
                   :cycles 2}

                  {:opcode 0x3A
                   :address-mode :implied
                   :cycles 2}

                  {:opcode 0x5A
                   :address-mode :implied
                   :cycles 2}

                  {:opcode 0x7A
                   :address-mode :implied
                   :cycles 2}

                  {:opcode 0xDA
                   :address-mode :implied
                   :cycles 2}

                  {:opcode 0xFA
                   :address-mode :implied
                   :cycles 2}]}

   ; Undocumented.
   "TOP" {:function status/nop-opfn
          :mutates-memory false
          :codes [{:opcode 0x0C
                   :address-mode :absolute
                   :cycles 4}

                  {:opcode 0x1C
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x3C
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x5C
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0x7C
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0xDC
                   :address-mode :absolutex
                   :cycles 4}

                  {:opcode 0xFC
                   :address-mode :absolutex
                   :cycles 4}]}})

(def instruction-set
  (->> instructions
       (keys)
       (map (fn [key]
              (let [instruction (get instructions key)]
                (->> (:codes instruction)
                     (map (fn [code]
                            {:name key
                             :function (:function instruction)
                             :mutates-memory (:mutates-memory instruction)
                             :opcode (:opcode code)
                             :address-mode (:address-mode code)
                             :cycles (:cycles code)}))))))
       (flatten)
       (map (fn [x]
              {(:opcode x) x}))
       (reduce (fn [x y] (merge x y)))))
