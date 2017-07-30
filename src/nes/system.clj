(ns nes.system
  (:require [nes.opcodes :refer :all]
            [nes.memory :as mem]
            [nes.debug :as debug]))

(defn new-system []
  { :acc           0x00
    :x             0x00
    :y             0x00
    :pc            0x0000
    :sp            0xFD

    :carry-flag    false
    :zero-flag     false
    :int-flag      true
    :dec-flag      false

    :brk-flag      false
    :unused-flag   true
    :overflow-flag false
    :sign-flag     false

    :cycle-count   0
    :mem          (mem/new-memory)})

(defn update-pc
  [system instruction]
  (update system :pc
    (fn [pc]
      (-> instruction
          (:address-mode)
          (operand-sizes)
          (inc)
          (+ pc)))))

(defn- cross-boundary-cycle
  [system instruction resolved-address]
  (let [operand (:operand instruction)
        mode (:address-mode instruction)
        address
          (case mode
            :absolutex operand
            :absolutey operand
            :indirecty (mem/indirect-address (:mem system) operand)
            nil)]
    (if (and address
             (not (mem/same-page? resolved-address address)))
      1
      0)))

(defn- execute-opfn [system instruction]
  (let [opfn (:function instruction)
        resolved-address (mem/resolve-address system instruction)]
    (if (:mutates-memory instruction)
       (opfn system (mem/resolve-address system instruction))
       (let [extra-tick (cross-boundary-cycle system instruction resolved-address)]
         (-> system
             (opfn (mem/read-from-memory system instruction resolved-address))
             (update :cycle-count #(+ % extra-tick)))))))

(defn read-operand
  "Gets an operand form a given address given a
  size (0, 1, or 2 bytes)"
  [mem addr size]
  (case size
    0 nil
    1 (get mem addr)
    2 (mem/combine-bytes
        (get mem (inc addr))
        (get mem addr))))

(defn get-current-instruction
  [system]
  (let [opcode (get (:mem system) (:pc system))
        instruction (get instruction-set opcode)
        _ (if (nil? instruction)
            (println (format "ERROR: opcode %02X does not correspond with a known instruction" opcode)))
        operand-size ((:address-mode instruction) operand-sizes)
        operand (read-operand
                  (:mem system)
                  (inc (:pc system))
                  operand-size)]
    (assoc instruction :operand operand)))



(defn- validate-instruction
  [instruction]
  ())

(defn execute
  "execute takes a system value and executes the next instruction,
  return a new system. This can be thought as the heart of the CPU
  emulator."
  [system]
  (let [mem (:mem system)
        instruction (get-current-instruction system)
        cycles (:cycles instruction)]
    (-> system
        (update-pc instruction)
        (execute-opfn instruction)
        (update :cycle-count #(+ cycles %)))))


(defn run
  [system iterations]
  (loop [sys system count 0]
    (if (and (not (= iterations :forever))
             (>= count iterations))
      sys
      (recur (execute sys) (inc count)))))
