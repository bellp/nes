(ns nes.memory
  (require [nes.mapper :as mapper]))

(defn new-memory
  "Creates a new 64kb memory space."
  []
  (->> (repeat 65536 0x00)
       (vec)))

(defn most-significant-byte [value]
  (-> value
      (bit-shift-right 8)
      (bit-and 0xFF)))

(defn combine-bytes [msb lsb]
  (-> msb
      (bit-shift-left 8)
      (bit-or lsb)))

(defn push8 [system value]
  (-> system
      (mapper/write8 (bit-or 0x100 (:sp system)) value)
      (update :sp dec)))

(defn push16 [system value]
  (let [msb (most-significant-byte value)
        lsb (bit-and 0xFF value)
        top (bit-or (:sp system) 0x100)
        top-1 (dec (bit-or (:sp system) 0x100))]
    (-> system
        (mapper/write8 top msb)
        (mapper/write8 top-1 lsb)
        (update :sp (fn [sp] (bit-and 0xFF (- sp 2)))))))

(defn read16
  [system addr]
  (let [[lsb _] (mapper/read8 system addr)
        [msb _] (mapper/read8 system (inc addr))]
    (combine-bytes msb lsb)))

(defn write16 [system address value]
  (let [msb (most-significant-byte value)
        lsb (bit-and value 0xFF)]
    (-> system
        (mapper/write8 address lsb)
        (mapper/write8 (bit-and 0xFFFF (inc address)) msb))))

(defn read8
  "Reads an 8-bit value from a given address (or accumulator)"
  [system address]
  (case address
    :accumulator [(:acc system) system]
    (mapper/read8 system address)))

(defn peek8
  [system address]
  (let [[value _] (mapper/read8 system address)]
    value))

(defn write8 [system address value]
  (case address
    :accumulator (assoc system :acc (bit-and 0xFF value))
    (mapper/write8 system address (bit-and 0xFF value))))

(defn read-last-pushed-byte
  [system]
  (peek8 system (-> (:sp system)
                    (inc)
                    (bit-and 0xFF)
                    (bit-or 0x100))))

(defn single-page-read16
  [system address]
  (let [[msb _] (mapper/read8 system (bit-and (inc address) 0xFF))
        [lsb _] (mapper/read8 system (bit-and address 0xFF))]
    (combine-bytes msb lsb)))

(defn indirect-address
  [system operand]
  (let [[msb _] (mapper/read8 system (bit-or (bit-and operand 0xFF00) (bit-and (inc operand) 0xFF)))
        [lsb _] (mapper/read8 system (bit-and operand 0xFFFF))]
    (combine-bytes msb lsb)))

(defn indirect-y
  [system operand]
  (-> system
      (single-page-read16 operand)
      (+ (:y system))
      (bit-and 0xFFFF)))

(defn indirect-x
  [system operand]
  (let [x-offset (bit-and (+ operand (:x system)) 0xFF)]
    (-> system
        (single-page-read16 x-offset))))

(defn zeropage-reg8
  [operand reg]
  (bit-and (+ reg operand)
           0xFF))

(defn absolute-reg16
  [operand reg]
  (bit-and (+ reg operand)
           0xFFFF))

(defn same-page?
  "Returns whether two addresses are within the same page."
  [addr1 addr2]
  (= (bit-and 0xFF00 addr1)
     (bit-and 0xFF00 addr2)))

(defn resolve-address
  [system instruction]
  (let [mode (:address-mode instruction)
        operand (:operand instruction)]
    (case mode
      :accumulator :accumulator
      :immediate   nil
      :implied     nil
      :relative    operand
      :zeropage    operand
      :zeropagex   (zeropage-reg8 operand (:x system))
      :zeropagey   (zeropage-reg8 operand (:y system))
      :absolute    operand
      :absolutex   (absolute-reg16 operand (:x system))
      :absolutey   (absolute-reg16 operand (:y system))
      :indirect    (indirect-address system operand)
      :indirectx   (indirect-x system operand)
      :indirecty   (indirect-y system operand))))

(defn read-from-memory
  "Reads a value from memory for a given instruction."
  [system instruction address]
  (let [mode (:address-mode instruction)]
    (case mode
      :accumulator [system nil]
      :implied [nil system]
      :relative [(:operand instruction) system]
      :immediate [(:operand instruction) system]
      (mapper/read8 system address))))

