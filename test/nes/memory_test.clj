(ns nes.memory-test
    (:use [midje.sweet]
          [nes.system]
          [nes.opcodes]
          [nes.memory]))

(fact "address calculates zeropage addresses"
  (-> (new-system)
      (assoc-in [:mem 0x3F] 0x11)
      (read-from-memory { :address-mode :zeropage
                         :operand 0x3F })) => 0x11)

(fact "address calculates zeropage-reg8 addressing modes"
   (zeropage-reg8 0x01 0x01) => 0x02
   (zeropage-reg8 0xFF 0x01) => 0x00)

(fact "address calculates absolute addresses"
  (-> (new-system)
      (assoc-in [:mem 0x1234] 0x33)
      (read-from-memory { :address-mode :absolute
                         :operand 0x1234 })) => 0x33)

(fact "address calculates absolute-reg16 addresses"
   (absolute-reg16 0x0001 0x0001) => 0x0002
   (absolute-reg16 0xFFFF 0x0001) => 0x0000)

(fact "address calculates indirect addresses"
  (-> (new-system)
      (assoc-in [:mem 0x1234] 0xCD)
      (assoc-in [:mem 0x1235] 0xAB)
      (assoc-in [:mem 0xABCD] 0xEE)
      (read-from-memory { :address-mode :indirect
                          :operand 0x1234 })) => 0xEE)

(fact "address calculates (indirect+x) addresses"
  (-> (new-system)
      (assoc :x 0x01)
      (assoc-in [:mem 0x12] 0xCD)
      (assoc-in [:mem 0x13] 0xAB)
      (assoc-in [:mem 0xABCD] 0xEE)
      (read-from-memory { :address-mode :indirectx
                          :operand 0x11 })) => 0xEE

  (-> (new-system)
      (assoc :x 0x01)
      (assoc-in [:mem 0x00] 0xCD)
      (assoc-in [:mem 0x01] 0xAB)
      (assoc-in [:mem 0xABCD] 0x33)
      (read-from-memory { :address-mode :indirectx
                          :operand 0xFF })) => 0x33)

(fact "address calculates (indirect)+y addresses"
  (-> (new-system)
      (assoc :y 0x01)
      (assoc-in [:mem 0x34] 0xCD)
      (assoc-in [:mem 0x35] 0xAB)
      (assoc-in [:mem 0xABCE] 0x33)
      (read-from-memory { :address-mode :indirecty
                          :operand 0x34 })) => 0x33

  (-> (new-system)
      (assoc :y 0x05)
      (assoc-in [:mem 0x34] 0xFF)
      (assoc-in [:mem 0x35] 0xFF)
      (assoc-in [:mem 0x0004] 0x33)
      (read-from-memory { :address-mode :indirecty
                          :operand 0x34 })) => 0x33)

(fact "get-current-instruction returns instruction with current opcode"
  (-> (new-system)
      (assoc-in [:mem 0x00] 0x75)
      (get-current-instruction)
      (:opcode)) => 0x75)

(fact "get-current-instruction returns instruction with current 8-bit operand"
  (-> (new-system)
      (assoc-in [:mem 0x00] 0x75)
      (assoc-in [:mem 0x01] 0x55)
      (get-current-instruction)
      (:operand)) => 0x55)

(fact "get-current-instruction returns instruction with current 16-bit operand"
  (-> (new-system)
      (assoc-in [:mem 0x00] 0x6D)
      (assoc-in [:mem 0x01] 0x34)
      (assoc-in [:mem 0x02] 0x12)
      (get-current-instruction)
      (:operand)) => 0x1234)

(fact "get-current-instruction returns instruction with correct name"
  (-> (new-system)
      (assoc-in [:mem 0x00] 0x75)
      (get-current-instruction)
      (:name)) => "ADC")

(fact "read-operand can return an 8-bit value in memory"
  (-> (new-memory)
      (assoc 0x1234 0x55)
      (read-operand 0x1234 1)) => 0x55)

(fact "read-operand can return an 8-bit value in memory"
  (-> (new-memory)
      (assoc 0x1234 0xCD)
      (assoc 0x1235 0xAB)
      (read-operand 0x1234 2)) => 0xABCD)

(fact "read-operand returns nil if operand-size is 0 (immediate address mode)"
  (-> (new-memory)
      (read-operand 0x0000 0)) => nil)

(fact "same-page? returns whether two addresses are within the same 256 byte page"
  (same-page? 0x00 0xFF) => true
  (same-page? 0x3F 0x13F) => false
  (same-page? 0xFF 0xFF) => true
  (same-page? 0x00 0x00) => true
  (same-page? 0x083F 0x082C) => true
  (same-page? 0x0200 0x0100) => false)
