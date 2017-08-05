(ns nes.debug)

(defn inspect [x]
  (let [fmt (if (integer? x)
              "%X"
              "%s")]
    (println (format fmt x))
    x))

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
        (bit-digit (:carry-flag system))))
  system)

; (defn- spaces [n]
;   (->> (range n)
;        (map (fn [_] " "))
;        (reduce str)))

; (defn- argument
;   [instruction]
;   (let [address-mode (:address-mode instruction)]
;     (case (address-mode operand-sizes)
;       0 (spaces 28)
;       1 (if (= address-mode :immediate)
;           (format "#$%02X%s" (:operand instruction) (spaces 24))
;           (format "$%02X%s" (:operand instruction) (spaces 25)))
;       2 (format "$%04X%s" (:operand instruction) (spaces 23)))))


; (defn- opcode-and-operands
;   [instruction]
;   (let [operand-size ((:address-mode instruction) operand-sizes)]
;     (case operand-size
;       0 (format "%02X%s" (:opcode instruction) (spaces 8))
;       1 (format "%02X %02X%s" (:opcode instruction) (:operand instruction) (spaces 5))
;       2 (format "%02X %02X %02X%s"
;           (:opcode instruction)
;           (bit-and 0xFF (:operand instruction))
;           (bit-and 0xFF (bit-shift-right (:operand instruction) 8))
;           (spaces 2)))))

; (defn show-state
;   [system instruction]
;   (format "%04X  %s%s %sA:%02X X:%02X Y:%02X P:%02X SP:%02X CYC:%d"
;     (:pc system)
;     (opcode-and-operands instruction)
;     (:name instruction)
;     (argument instruction)
;     (:acc system)
;     (:x system)
;     (:y system)
;     (status/get-status system)
;     (:sp system)
;     (rem (* 3 (:cycle-count system)) 341)))

