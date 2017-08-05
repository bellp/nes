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
      (:num-prg-rom-banks)) => 1)

(fact "header contains number of CHR ROM 8 kb units"
  (-> nestest/rom
      (read-header)
      (:num-chr-rom-banks)) => 1)

(fact "write-into works"
  (write-into [0 1 2 3 4 5 6 7 8 9]
              4
              [42 43]) => [0 1 2 3 42 43 6 7 8 9])

(fact "If ROM control byte 1/bit 0 is clear then rom uses horizontal mirroring"
  (-> nestest/rom
      (read-header)
      (:mirroring)) => :horizontal)

(fact "If ROM control byte 1/bit 0 is set then rom uses vertical mirroring"
  (-> nestest/rom
      (assoc 6 0x01)
      (read-header)
      (:mirroring)) => :vertical)

(fact "If ROM control byte 1/bit 1 is clear then rom uses battery-packed RAM at 0x6000-0x7FFF."
  (-> nestest/rom
      (read-header)
      (:battery?)) => false)

(fact "If ROM control byte 1/bit 1 is set then rom uses battery-packed RAM at 0x6000-0x7FFF."
  (-> nestest/rom
      (assoc 6 0x02)
      (read-header)
      (:battery?)) => true)

(fact "If ROM control byte 1/bit 2 is set then rom has a 512-byte trainer at 0x7000-0x71FF"
  (-> nestest/rom
      (read-header)
      (:trainer?)) => false)

(fact "If ROM control byte 1/bit 2 is set then rom doesn't use a 512-byte trainer at 0x7000-0x71FF"
  (-> nestest/rom
      (assoc 6 0x04)
      (read-header)
      (:trainer?)) => true)

(fact "ROM control byte 1/bits 4-7 and control byte 2/bits 4-7 indicate the mapper number used"
  (-> nestest/rom
      (assoc 6 0x30)
      (assoc 7 0xE0)
      (read-header)
      (:mapper)) => 0xE3)

(fact "read-prg-banks reads a ROM's PRG banks into a map"
  (->> (read-header nestest/rom)
       (read-prg-banks nestest/rom)
       (keys)) => [0]

  (->> (read-header nestest/rom)
       (read-prg-banks nestest/rom)
       (vals)
       (flatten)
       (count)) => 0x4000)

(fact "read-chr-banks reads a ROM's CHR banks into a map"
  (->> (read-header nestest/rom)
       (read-chr-banks nestest/rom)
       (keys)) => [0]

  (->> (read-header nestest/rom)
       (read-chr-banks nestest/rom)
       (vals)
       (flatten)
       (count)) => 0x2000)

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
        num-rows (count log)
        system (-> (sys/boot (read-rom nestest/rom))
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
                              :operand-size ((:address-mode instruction) op/operand-sizes)}]
        (cond
          (not (= converted-system (log row)))
          (do
            (println (format "On line %d:" (inc row)))
            (println (format "Expected : %s" (sys->text (log row))))
            (println (format "Actual   : %s" (sys->text converted-system)))
            converted-system)

          (>= row (dec num-rows)) (println "SUCCESS!")
          :else
            (recur (sys/execute s) (inc row)))))))
