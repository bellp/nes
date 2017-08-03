(ns nes.system-test
  (:require [midje.sweet :refer :all]
            [nes.assembly :refer :all]
            [nes.system :refer :all]
            [nes.mapper :as mapper]))

(fact "end-to-end test"
  (-> (new-system)
      (assoc :acc 0x03)
      (compile-statement "ADC #$55")
      (assoc :pc 0x0000)
      (execute)
      (:acc)) => 0x58)

(fact "end-to-end test (instruction mutates memory)"
  (-> (new-system)
      (compile-statement "INC $1234")
      (assoc :pc 0x0000)
      (execute)
      (mapper/read8 0x1234)) => 1)

(fact "end-to-end test (instruction mutates memory)"
  (-> (new-system)
      (compile-statement "INC $1234")
      (assoc :pc 0x0000)
      (execute)
      (mapper/read8 0x1234)) => 1)

(fact "execute updates cycle-count after executing an instruction"
  (-> (new-system)
      (compile-statement "ASL $1000,X")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 7)

(fact "execute adds an extra +1 to cycle-count iff Absolute,X crosses a page"
  (-> (new-system)
      (assoc :x 0x01)
      (compile-statement "ADC $08FF,X")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 5

  (-> (new-system)
      (assoc :x 0x00)
      (compile-statement "ADC $08FF,X")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 4)

(fact "execute adds an extra +1 to cycle-count iff Absolute,Y crosses a page"
  (-> (new-system)
      (assoc :y 0x01)
      (compile-statement "ADC $08FF,Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 5

  (-> (new-system)
      (assoc :y 0x00)
      (compile-statement "ADC $08FF,Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 4)

(fact "execute adds an extra +1 to cycle-count iff (Indirect),Y crosses a page"
  (-> (new-system)
      (mapper/write8 0x0010 0xFF)
      (mapper/write8 0x0011 0x08)
      (assoc :y 0x01)
      (compile-statement "ADC ($10),Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 6

  (-> (new-system)
      (mapper/write8 0x00 0xFF)
      (mapper/write8 0x01 0x08)
      (assoc :y 0x00)
      (compile-statement "ADC ($00),Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 5)

(fact "read-operand can return an 8-bit value in memory"
  (-> (new-system)
      (mapper/write8 0x1234 0x55)
      (mapper/write8 0x00 0x6D)
      (read-operand 0x1234 1)) => 0x55)

(fact "read-operand can return an 8-bit value in memory"
  (-> (new-system)
      (mapper/write8 0x1234 0xCD)
      (mapper/write8 0x1235 0xAB)
      (read-operand 0x1234 2)) => 0xABCD)

(fact "read-operand returns nil if operand-size is 0 (immediate address mode)"
  (-> (new-system)
      (read-operand 0x0000 0)) => nil)


; (fact "perf test"
;   (let [system (-> (new-system)
;                    (compile-statement "ADC ($10),Y")
;                    (assoc :pc 0x0000))]
;     (time
;       (dotimes [n 600000] (fn [_] (execute system))))))


