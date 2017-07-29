(ns nes.rom-test
  (require [midje.sweet :refer :all]
           [nes.debug :as debug]
           [nes.opcodes :as op]
           [nes.status :as status]
           [nes.nestest :as nestest]
           [nes.memory :as mem]
           [nes.rom :refer :all]
           [nes.system :as sys]
           [clojure.string :as str]))

(fact "header contains NES preamble"
  (-> nestest/rom
      (read-header)
      (:preamble)) => "NES")

(fact "header contains number of PRG ROM 16 kb units"
  (-> nestest/rom
      (read-header)
      (:prg-rom-units)) => 1)

(fact "header contains number of CHR ROM 8 kb units"
  (-> nestest/rom
      (read-header)
      (:chr-rom-units)) => 1)

(fact "read-prg reads 16384 bytes"
  (-> nestest/rom
      (read-prg (read-header nestest/rom))
      (count)) => 16384)

(fact "write-into works"
  (write-into [0 1 2 3 4 5 6 7 8 9]
              4
              [42 43]) => [0 1 2 3 42 43 6 7 8 9])


(defn- opcode-and-operands
  [s]
  (let [operand-size (:operand-size s)]
    (case operand-size
      0 (format "%02X" (:opcode s))
      1 (format "%02X %02X" (:opcode s) (:operand s))
      2 (format "%02X %02X %02X"
          (:opcode s)
          (bit-and 0xFF (:operand s))
          (bit-and 0xFF (bit-shift-right (:operand s) 8))))))

(defn- sys->text
  [system]
  (format "%04X   %s      A:%02X X:%02X Y:%02X P:%02X SP:%02X CYC:%d"
    (:pc system)
    (opcode-and-operands system)
    (:acc system)
    (:x system)
    (:y system)
    (:p system)
    (:sp system)
    (:cyc system)))


(defn- hexstr->int
  [str]
  (Integer/parseInt str 16))

(def nestest-regex
  #"(\p{XDigit}{4})  ((\p{XDigit}{2}\s){1,3})+.+A:(\p{XDigit}{2}) X:(\p{XDigit}{2}) Y:(\p{XDigit}{2}) P:(\p{XDigit}{2}) SP:(\p{XDigit}{2}) CYC:\s*(\d+)$")

(defn- args
  [x]
  (let [trimmed (str/trim x)]
    (case (count trimmed)
      2 {:opcode (hexstr->int trimmed)
         :operand-size 0
         :operand nil}
      5 {:opcode (->> trimmed (take 2) (reduce str) hexstr->int)
         :operand-size 1
         :operand (->> trimmed (drop 3) (take 2) (reduce str) hexstr->int)}
      8 {:opcode (->> trimmed (take 2) (reduce str) hexstr->int)
         :operand-size 2
         :operand (mem/combine-bytes
                    (->> trimmed (drop 6) (take 2) (reduce str) hexstr->int)
                    (->> trimmed (drop 3) (take 2) (reduce str) hexstr->int))})))

(defn extract-values
  [line]
  (let [matches (re-matches nestest-regex line)
        len (count matches)]
    (-> {:pc (hexstr->int (matches 1))}
        (merge (args (matches 2)))
        (assoc :acc (hexstr->int (matches (- len 6))))
        (assoc :x (hexstr->int (matches (- len 5))))
        (assoc :y (hexstr->int (matches (- len 4))))
        (assoc :p (hexstr->int (matches (- len 3))))
        (assoc :sp (hexstr->int (matches (- len 2))))
        (assoc :cyc (Long/parseLong (matches (- len 1)))))))

(defn- parse-nestest-file [path]
  (->> (slurp path)
       (str/split-lines)
       (map extract-values)
       (vec)))

(fact "emulator matches nestest log" :integration
  (let [log (parse-nestest-file "nestest.log")
        prg-rom (read-prg nestest/rom (read-header nestest/rom))
        system (-> (sys/new-system)
                   (update :mem (fn [m] (write-into m 0xC000 prg-rom)))
                   (update :mem (fn [m] (write-into m 0x8000 prg-rom)))
                   (assoc :pc 0xC000))]
    (loop [s system row 0]
      (let [instruction (sys/get-current-instruction s)
            converted-system {:acc (:acc s)
                              :pc (:pc s)
                              :x (:x s)
                              :y (:y s)
                              :p (status/get-status s)
                              :sp (:sp s)
                              :cyc (rem (* 3 (:cycle-count s)) 341)
                              :opcode (:opcode instruction)
                              :operand (:operand instruction)
                              :operand-size ((:address-mode instruction) op/operand-sizes)}

            _ (println (format "Line %d: %s" (inc row) (sys->text converted-system)))]

        (if (not (= converted-system (log row)))
          (do
            (println (format "On line %d:" (inc row)))
            (println (format "Expected : %s" (sys->text (log row))))
            (println (format "Actual   : %s" (sys->text converted-system))))
          (recur (sys/execute s) (inc row)))))))






; (fact "Can parse lines" :integration
;   (let [states (parse-nestest-file "nestest.log")]
;     (first states) => {:pc 0xC000
;                         :opcode 0x4C
;                         :operand 0xC5F5
;                         :acc 0x00
;                         :x 0x00
;                         :y 0x00
;                         :p 0x24
;                         :sp 0xFD
;                         :cyc 0
;                         :operand-size 2}

;     (states 6) => {:pc 0xC72D
;                    :opcode 0xEA
;                    :operand nil
;                    :acc 0x00
;                    :x 0x00
;                    :y 0x00
;                    :p 0x26
;                    :sp 0xFB
;                    :cyc 60
;                    :operand-size 0}

;     (states 8) => {:pc 0xC72F
;                    :opcode 0xB0
;                    :operand 0x04
;                    :acc 0x00
;                    :x 0x00
;                    :y 0x00
;                    :p 0x27
;                    :sp 0xFB
;                    :cyc 72
;                    :operand-size 1}))



(fact "run nestest" :integration
  (let [prg-rom (read-prg nestest/rom (read-header nestest/rom))]
    (-> (sys/new-system)
        (update :mem (fn [m] (write-into m 0xC000 prg-rom)))
        (update :mem (fn [m] (write-into m 0x8000 prg-rom)))
        (assoc :pc 0xC000)
        (sys/run 887)
        (mem/read16 0x0002)) => 0x0000))




