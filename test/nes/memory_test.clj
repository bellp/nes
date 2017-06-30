(ns nes.memory-test
    (:use [midje.sweet]
          [nes.system]
          [nes.opcodes]
          [nes.memory]))

(fact "address calculates zeropage addresses"
  (-> (new-system)
      (address :zeropage 0x3F)) => 0x3F
  (-> (new-system)
      (address :zeropage 0x1FF)) => 0xFF)

(fact "address calculates zeropage-reg8 addressing modes"
   (-> (new-system)
       (assoc-in [:mem 0x11] 0x77)
       (zeropage-reg8 0x10 0x01)) => 0x77

  (-> (new-system)
      (assoc-in [:mem 0x00] 0x33)
      (zeropage-reg8 0xFF 0x01)) => 0x33)

(fact "address calculates absolute addresses"
  (-> (new-system)
      (address :absolute 0x1234)) => 0x1234
  (-> (new-system)
      (address :absolute 0x12345)) => 0x2345)

(fact "address calculates absolute-reg16 addresses"
  (-> (new-system)
      (assoc-in [:mem 0x1235] 0x77)
      (absolute-reg16 0x1234 0x01)) => 0x77

  (-> (new-system)
      (assoc-in [:mem 0x0000] 0x33)
      (absolute-reg16 0xFFFF 0x01)) => 0x33)

(fact "address calculates indirect addresses"
  (-> (new-system)
      (assoc-in [:mem 0x1234] 0xCD)
      (assoc-in [:mem 0x1235] 0xAB)
      (address :indirect 0x1234)) => 0xABCD)

(fact "address calculates (indirect+x) addresses"
  (-> (new-system)
      (assoc :x 0x01)
      (assoc-in [:mem 0x35] 0xCD)
      (assoc-in [:mem 0x36] 0xAB)
      (address :indirectx 0x34)) => 0xABCD
  (-> (new-system)
      (assoc :x 0x05)
      (assoc-in [:mem 0x04] 0xCD)
      (assoc-in [:mem 0x05] 0xAB)
      (address :indirectx 0xFF)) => 0xABCD)

  (fact "address calculates (indirect)+y addresses"
    (-> (new-system)
        (assoc :y 0x01)
        (assoc-in [:mem 0x34] 0xCD)
        (assoc-in [:mem 0x35] 0xAB)
        (address :indirecty 0x34)) => 0xABCE
    (-> (new-system)
        (assoc :y 0x05)
        (assoc-in [:mem 0x04] 0xFE)
        (assoc-in [:mem 0x05] 0xFF)
        (address :indirecty 0x04)) => 0x0003)

; (fact "get-operand returns nil of size is 0"
;   (-> (new-memory)
;       (get-operand 0x0000, 0)) => nil)

; (fact "get-operand returns single byte from memory if size is 1"
;   (-> (new-memory)
;       (assoc 0x01 0xAB)
;       (assoc 0x02 0xCD)
;       (get-operand 0x0001 1)) => 0xAB)

; (fact "get-operand returns two bytes from memory if size is 2"
;   (-> (new-memory)
;       (assoc 0x01 0xAB)
;       (assoc 0x02 0xCD)
;       (get-operand 0x01 2)) => 0xCDAB)
