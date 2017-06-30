(ns nes.core
  (:use [nes.system]
        [nes.opcodes]
        [nes.debug]
        [nes.memory]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
