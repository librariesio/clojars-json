(defproject clj2json "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.4.0"]
                 [compojure "1.3.2"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-jetty-adapter "1.2.1"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler clj2json.core/app}
  :profiles {:uberjar {:aot :all}}
  :main clj2json.core
  )
