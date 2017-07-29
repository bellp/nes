(ns nes.system-test
  (:require [midje.sweet :refer :all]
            [nes.assembly :refer :all]
            [nes.system :refer :all]))

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
      (get-in [:mem 0x1234])) => 1)

(fact "end-to-end test (instruction mutates memory)"
  (-> (new-system)
      (compile-statement "INC $1234")
      (assoc :pc 0x0000)
      (execute)
      (get-in [:mem 0x1234])) => 1)

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
      (assoc-in [:mem 0x10] 0xFF)
      (assoc-in [:mem 0x11] 0x08)
      (assoc :y 0x01)
      (compile-statement "ADC ($10),Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 6

  (-> (new-system)
      (assoc-in [:mem 0x00] 0xFF)
      (assoc-in [:mem 0x01] 0x08)
      (assoc :y 0x00)
      (compile-statement "ADC ($00),Y")
      (assoc :pc 0x0000)
      (execute)
      (:cycle-count)) => 5)


; (fact "perf test"
;   (let [system (-> (new-system)
;                    (compile-statement "ADC ($10),Y")
;                    (assoc :pc 0x0000))]
;     (time
;       (dotimes [n 600000] (fn [_] (execute system))))))


