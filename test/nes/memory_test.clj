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
   (-> (new-system)
       (assoc-in [:mem 0x11] 0x77)
       (zeropage-reg8 0x10 0x01)) => 0x77

  (-> (new-system)
      (assoc-in [:mem 0x00] 0x33)
      (zeropage-reg8 0xFF 0x01)) => 0x33)

(fact "address calculates absolute addresses"
  (-> (new-system)
      (assoc-in [:mem 0x1234] 0x33)
      (read-from-memory { :address-mode :absolute
              :operand 0x1234 })) => 0x33)

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
