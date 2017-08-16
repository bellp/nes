(ns nes.memory-test
    (require [nes.mapper :as mapper])
    (:use [midje.sweet]
          [nes.system]
          [nes.opcodes]
          [nes.memory]))

(defn- read-byte
  [system addr]
  (let [[value sys-after-read] (read8 system addr)]
    value))

(fact "address calculates zeropage addresses"
  (-> (new-system)
      (resolve-address {:address-mode :zeropage
                        :operand 0x3F })) => 0x3F)

(fact "address calculates zeropage-reg8 addressing modes"
   (zeropage-reg8 0x01 0x01) => 0x02
   (zeropage-reg8 0xFF 0x01) => 0x00)

(fact "address calculates absolute addresses"
  (-> (new-system)
      (resolve-address {:address-mode :absolute
                        :operand 0x1234 })) => 0x1234)

(fact "address calculates absolute-reg16 addresses"
   (absolute-reg16 0x0001 0x0001) => 0x0002
   (absolute-reg16 0xFFFF 0x0001) => 0x0000)

(fact "address calculates indirect addresses"
  (-> (new-system)
      (mapper/write8 0x1234 0xCD)
      (mapper/write8 0x1235 0xAB)
      (indirect-address 0x1234)) => 0xABCD)

(fact "indirect-address handles a bug where an address like xxFF for the lsb uses xx00 for the msb"
  (-> (new-system)
      (mapper/write8 0x02FF 0x34)
      (mapper/write8 0x0200 0x12)
      (indirect-address 0x2FF)) => 0x1234)

(fact "address calculates (indirect+x) addresses"
  (-> (new-system)
      (assoc :x 0x01)
      (mapper/write8 0x12 0xCD)
      (mapper/write8 0x13 0xAB)
      (indirect-x 0x11)) => 0xABCD

  (-> (new-system)
      (assoc :x 0x01)
      (mapper/write8 0x00 0xCD)
      (mapper/write8 0x01 0xAB)
      (indirect-x 0xFF)) => 0xABCD)

(fact "address calculates (indirect)+y addresses"
  (-> (new-system)
      (assoc :y 0x01)
      (mapper/write8 0x34 0xCD)
      (mapper/write8 0x35 0xAB)
      (indirect-y 0x34)) => 0xABCE

  (-> (new-system)
      (assoc :y 0x05)
      (mapper/write8 0x34 0xFF)
      (mapper/write8 0x35 0xFF)
      (indirect-y 0x34)) => 0x0004)

(fact "get-current-instruction returns instruction with current opcode"
  (-> (new-system)
      (mapper/write8 0x00 0x75)
      (get-current-instruction)
      (:opcode)) => 0x75)

(fact "get-current-instruction returns instruction with current 8-bit operand"
  (-> (new-system)
      (mapper/write8 0x00 0x75)
      (mapper/write8 0x01 0x55)
      (get-current-instruction)
      (:operand)) => 0x55)

(fact "get-current-instruction returns instruction with current 16-bit operand"
  (-> (new-system)
      (mapper/write8 0x00 0x6D)
      (mapper/write8 0x01 0x34)
      (mapper/write8 0x02 0x12)
      (get-current-instruction)
      (:operand)) => 0x1234)

(fact "get-current-instruction returns instruction with correct name"
  (-> (new-system)
      (mapper/write8 0x00 0x75)
      (get-current-instruction)
      (:name)) => "ADC")

(fact "same-page? returns whether two addresses are within the same 256 byte page"
  (same-page? 0x00 0xFF) => true
  (same-page? 0x3F 0x13F) => false
  (same-page? 0xFF 0xFF) => true
  (same-page? 0x00 0x00) => true
  (same-page? 0x083F 0x082C) => true
  (same-page? 0x0200 0x0100) => false)

(fact "write16 can write a 16-bit value to memory"
  (let [system (-> (new-system)
                   (write16 0x1000 0xABCD))]
     (read-byte system 0x1000) => 0xCD
     (read-byte system 0x1001) => 0xAB))

(fact "read16 reads a 16-bit value from a given address"
  (-> (new-system)
      (mapper/write8 0x1000 0x34)
      (mapper/write8 0x1001 0x12)
      (read16 0x1000)) => 0x1234)

(fact "push16 pushes a 16-bit value (such as an address) onto the stack"
  (-> (new-system)
      (assoc :sp 0xFD)
      (push16 0x1234)
      (read-byte 0x1FD)) => 0x12

  (-> (new-system)
      (assoc :sp 0xFD)
      (push16 0x1234)
      (read-byte 0x1FC)) => 0x34)

(fact "push16 updates the stack pointer"
  (-> (new-system)
      (assoc :sp 0xFF)
      (push16 0x1234)
      (:sp)) => 0xFD)

(fact "push8 pushes an 8-bit value onto the stack"
  (-> (new-system)
      (assoc :sp 0xFD)
      (push8 0x33)
      (read-byte 0x1FD)) => 0x33)

(fact "push8 updates the stack pointer by one"
  (-> (new-system)
      (assoc :sp 0xFD)
      (push8 0x33)
      (:sp)) => 0xFC)

(fact "read-last-pushed-byte reads the top byte on the stack"
  (-> (new-system)
      (assoc :sp 0xFE)
      (mapper/write8 0x1FF 0x3F)
      (read-last-pushed-byte)) => 0x3F)