(ns nes.assembly
  (:require [clojure.string :as s])
  (:use [nes.opcodes]
        [nes.memory]))

(def expressions
  { :implied #"$"
    :accumulator #"A$"
    :immediate  #"\#\$([0-9a-fA-F]{2})$"
    :absolute  #"\$([0-9a-fA-F]{4})$"
    :zeropage #"\$([0-9a-fA-F]{2})$"
    :relative #"\$([0-9a-fA-F]{2})$"
    :absolutex #"\$([0-9a-fA-F]{4})\,[x|X]$"
    :absolutey #"\$([0-9a-fA-F]{4})\,[y|Y]$"
    :zeropagex  #"\$([0-9a-fA-F]{2})\,[x|X]$"
    :zeropagey #"\$([0-9a-fA-F]{2})\,[y|Y]$"
    :indirect #"\(\$([0-9a-fA-F]{4})\)$"
    :indirectx #"\(\$([0-9a-fA-F]{2})\,[x|X]\)$"
    :indirecty #"\(\$([0-9a-fA-F]{2})\)\,[y|Y]$"})

(def relative-only-instructions
  #{"BCC" "BCS" "BEQ" "BMI" "BNE" "BPL" "BVC" "BVS"})

(defn get-arg-info [arg]
  (case arg
    nil {:address-mode :implied}
    "A" {:address-mode :accumulator}
    (let [match (->> (keys expressions)
                    (map (fn [am] {:address-mode am
                                    :matches (re-matches (am expressions) arg)}))
                    (filter (fn [x] (not (nil? (:matches x)))))
                    (first))]
      (if match
        {:address-mode (:address-mode match)
         :operand (->> (:matches match)
                       (last)
                       (str "0x")
                       (read-string))}))))

(defn find-opcode [instruction-name address-mode]
  (->> (vals instruction-set)
       (filter #(and
                  (= instruction-name (:name %))
                  (= address-mode (:address-mode %))))
       (first)
       (:opcode)))

(defn valid-instruction-name?
  [instruction-name]
  (as-> (vals instruction-set) i
        (map #(:name %) i)
        (set i)
        (contains? i instruction-name)))

(defn get-instruction-name [statement]
  (-> statement
      (s/trim)
      (s/split #"\s")
      (first)))

(defn describe-instruction [statement]
  (let [instruction-parts (-> statement
                              (s/trim)
                              (s/split #"\s"))
        name (first instruction-parts)
        arg (if (> (count instruction-parts) 1)
              (last instruction-parts)
              nil)
        arg-info (get-arg-info arg)
        address-mode (if (contains? relative-only-instructions name)
                         :relative
                         (:address-mode arg-info))]
    { :name name
      :address-mode address-mode
      :opcode (find-opcode name address-mode)
      :operand (:operand arg-info)}))

(defn write-operand-to-memory [system instruction-info]
  (let [address-mode (:address-mode instruction-info)
        operand (:operand instruction-info)
        operand-size (address-mode operand-sizes)
        address (inc (:pc system))]
    (case operand-size
      1 (write8 system address operand)
      2 (write16 system address operand)
      system)))

(defn compile-statement [system statement]
  (let [instruction-info (describe-instruction statement)
        opcode (:opcode instruction-info)
        address-mode (:address-mode instruction-info)
        operand (:operand instruction-info)
        pc (:pc system)]
    (if (nil? address-mode) (println (format "ERROR: Unknown address mode for %s" statement)))
    (-> system
        (write8 pc opcode)
        (write-operand-to-memory instruction-info)
        (update :pc (fn [pc]
                        (bit-and
                          0xFFFF
                          (+ pc 1 (address-mode operand-sizes))))))))
