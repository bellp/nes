(ns nes.assembly
  (:require [clojure.string :as s])
  (:use [nes.opcodes]))

; ADC $10
; ADC #$10
; ADC $10,X
; (def immediate #"\#\$[0-9a-fA-F]{2}$")
; (def absolute #"\$[0-9a-fA-F]{4}$")
; (def zeropage #"\$[0-9a-fA-F]{2}$")
; (def relative zeropage)
; (def absolute-index-x #"\$([0-9a-fA-F]{4})\,[x|X]$")
; (def absolute-index-y #"\$([0-9a-fA-F]{4})\,[y|Y]$")
; (def zeropage-index-x #"\$([0-9a-fA-F]{2})\,[x|X]$")
; (def zeropage-index-y #"\$([0-9a-fA-F]{2})\,[y|Y]$")
; (def zeropage-indirect-index-x #"\(\$([0-9a-fA-F]{2})\,[x|X]\)$")
; (def zeropage-index-indirect-y #"\(\$([0-9a-fA-F]{2})\)\,[x|X]$")

(defn inspect [x]
  (println x)
  x)

(def expressions
  { :immediate  #"\#\$([0-9a-fA-F]{2})$"
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
                     (read-string))})))

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
        arg (last instruction-parts)
        arg-info (get-arg-info arg)
        address-mode (if (contains? relative-only-instructions name)
                         :relative
                         (:address-mode arg-info))]
    { :name name
      :address-mode address-mode
      :opcode (find-opcode name address-mode)
      :operand (:operand arg-info)}))

(defn compile-statement
  [statement]
  0)
