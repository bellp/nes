(ns nes.arithmetic-test
    (:require [midje.sweet :refer :all]
              [nes.arithmetic :refer :all]
              [nes.memory :refer :all]
              [nes.system :refer :all]))

(defn perform-sbc
    [acc m carry]
    (-> (new-system)
        (assoc :carry-flag carry)
        (assoc :acc acc)
        (sbc m)))

(defn perform-adc
    [acc m carry]
    (-> (new-system)
        (assoc :carry-flag carry)
        (assoc :acc acc)
        (adc m)))

(fact "adc sets the carry flag when sum is greater than 255"
    (-> (perform-adc 200 100 false)
        (get :carry-flag)) => true

    (-> (perform-adc 100 100 false)
        (get :carry-flag)) => false)

(fact "adc adds sum of ACC + M + Carry"
    (:acc (perform-adc 2 3 false))   => 5
    (:acc (perform-adc 2 3 true))    => 6
    (:acc (perform-adc 255 1 false)) => 0
    (:acc (perform-adc 255 1 true))  => 1)

(fact "adc sets the overflow flag when sum is negative in 2s compliment"
    (-> (perform-adc 0x01 0x01 false)
        (get :overflow-flag)) => false

    (-> (perform-adc 0x01 0xFF false)
        (get :overflow-flag)) => false

    (-> (perform-adc 0x7F 0x01 false)
        (get :overflow-flag)) => true

    (-> (perform-adc 0x80 0xFF false)
        (get :overflow-flag)) => true)

(fact "adc sets the zero flag when sum is 0, clears when sum is non-0"
    (-> (perform-adc 0x00 0x01 false)
        (get :zero-flag)) => false

    (-> (perform-adc 0x01 0xFF false)
        (get :zero-flag)) => true

    (-> (perform-adc 0x00 0x00 false)
        (get :zero-flag)) => true)

(fact "adc sets the negative flag when sum is negative (bit 7 is set)"
    (-> (perform-adc 0x01 0x01 false)
        (get :sign-flag)) => false

    (-> (perform-adc 0x7F 0x01 false)
        (get :sign-flag)) => true

    (-> (perform-adc 0xFF 0x01 false)
        (get :sign-flag)) => false)

(fact "dec subtracts 1 from 0x05 located at address 0x20, setting value to 0x04"
      (-> (new-system)
          (assoc-in [:mem 0x20] 0x05)
          (op-dec 0x20)
          (:mem)
          (get 0x20)) => 0x04

      (-> (new-system)
          (op-dec 0x20)
          (:mem)
          (get 0x20)) => 0xFF)

(fact "change-by-one modifies a value by one and limits results to 0-255"
    (change-by-one dec 0x00) => 0xFF
    (change-by-one dec 0x01) => 0x00
    (change-by-one dec 0xFF) => 0xFE
    (change-by-one inc 0x00) => 0x01
    (change-by-one inc 0xFF) => 0x00)

(fact "sbc instruction should return A - M - Carry"
    (:acc (perform-sbc 0x05 0x01 true))  => 0x04
    (:acc (perform-sbc 0x05 0x01 false)) => 0x03
    (:acc (perform-sbc 0x03 0x09 false)) => 0xF9
    (:acc (perform-sbc 0x03 0x09 true))  => 0xFA)

(fact "sbc sets the overflow flag when sum is negative in 2s compliment"
    (-> (perform-sbc 0x00 0x01 true)
        (get :overflow-flag)) => false

    (-> (perform-sbc 0x80 0x01 true)
        (get :overflow-flag)) => true

    (-> (perform-sbc 0x80 0x01 false)
        (get :overflow-flag)) => true

    (-> (perform-sbc 0x7F 0xFF true)
        (get :overflow-flag)) => true

    (-> (perform-sbc 0x7F 0xFF false)
        (get :overflow-flag)) => false)
