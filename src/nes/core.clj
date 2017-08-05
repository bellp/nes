(ns nes.core
  (require [nes.debug :as debug]
           [nes.rom :as rom]
           [nes.system :as sys]))

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [rom (-> (rom/read-file (first args))
                (rom/read-rom))]
    (-> (sys/boot rom)
        (debug/inspect))))
