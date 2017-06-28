(ns nes.system
  (:use [nes.cpu]
        [nes.opcodes]
        [nes.memory]))

(defn new-system []
  { :acc           0x00
    :x             0x00
    :y             0x00
    :pc            0x00
    :sp            0x00

    :carry-flag    false
    :zero-flag     false
    :overflow-flag false
    :int-flag      false
    :dec-flag      false
    :unused-flag   true
    :brk-flag      false
    :sign-flag     false

    :cycles        0
    :mem          (new-memory) })

(defn execute
  "execute takes a system value and executes the next instruction,
  return a new system. This can be thought as the heart of the CPU
  emulator."
  [system]
  (let [cpu (get system :cpu)
        mem (get system :mem)
        opcode (get mem (:pc cpu))
        instruction (opcode instructions)
        operand-size ((:address-mode instruction) operand-size)]
    "TBD"))
