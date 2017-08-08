(defproject nes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :main nes.core
  :profiles {:dev {:dependencies [[midje "1.8.3"]]} :midje {}}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-server"]

  :dependencies [[org.clojure/clojure "1.8.0"]]
  :debug-repl {:resource-paths ["/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home/lib/tools.jar"]
               :repl-options {:nrepl-middleware [debug-middleware.core/debug-middleware]}
               :dependencies [[org.clojure/clojure "1.8.0"]
                              [debug-middleware "0.1.2-SNAPSHOT"]]})
