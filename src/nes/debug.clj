(ns nes.debug)

(defn bit-digit
  [b]
  (if b "1" "0"))

(defn show-system
  [system]
  (print
    (format
      "-- CPU --\r\nACC: %02X     X: %02X    Y: %02X\r\nPC:  %04X  SP: %02X    Cycles: %d\r\nN V - B D I Z C\r\n%s %s %s %s %s %s %s %s\r\n---------\r\n"
        (:acc system)
        (:x system)
        (:y system)
        (:pc system)
        (:sp system)
        (:cycle-count system)
        (bit-digit (:sign-flag system))
        (bit-digit (:overflow-flag system))
        (bit-digit (:unused-flag system))
        (bit-digit (:brk-flag system))
        (bit-digit (:dec-flag system))
        (bit-digit (:int-flag system))
        (bit-digit (:zero-flag system))
        (bit-digit (:carry-flag system)))))