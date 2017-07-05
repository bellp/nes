(ns nes.system
  (:use [nes.cpu]
        [nes.opcodes]
        [nes.memory]))

(defn new-system []
  { :acc           0x00
    :x             0x00
    :y             0x00
    :pc            0x0000
    :sp            0xFF

    :carry-flag    false
    :zero-flag     false
    :overflow-flag false
    :int-flag      false
    :dec-flag      false
    :unused-flag   true
    :brk-flag      false
    :sign-flag     false

    :cycle-count   0
    :mem          (new-memory)})

(defn update-pc
  [system instruction]
  (update system :pc
    (fn [pc]
      (-> instruction
          (:address-mode)
          (operand-sizes)
          (inc)
          (+ pc)))))

(defn execute
  "execute takes a system value and executes the next instruction,
  return a new system. This can be thought as the heart of the CPU
  emulator."
  [system]
  (let [mem (:mem system)
        opcode (get mem (:pc system))
        instruction (get-current-instruction system)
        cycles (:cycles instruction)
        address-mode (:address-mode instruction)
        m (read-from-memory system instruction)
        opcode-fn (:function instruction)]
    (-> system
        (opcode-fn m)
        (update :cycle-count #(+ cycles %))
        (update-pc instruction))))