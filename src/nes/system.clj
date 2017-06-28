(ns nes.system
    (:use [nes.cpu]
          [nes.opcodes]
          [nes.memory]))

(defn new-system []
    { :cpu (new-cpu)
      :mem (new-memory) })

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
