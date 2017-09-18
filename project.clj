(defproject clojars-json "0.1.0"
  :description "A clojure service that transforms the clojars.org API responses into JSON"
  :url "https://github.com/librariesio/clojars-json"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.memoize "0.5.9"]
                 [cheshire "5.7.1"]
                 [compojure "1.6.0"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-jetty-adapter "1.5.1"]]
  :plugins [[lein-ring "0.8.13"]]
  :min-lein-version "2.0.0"
  :ring {:handler clojars-json.core/app}
  :uberjar-name "clojars-json.jar"
  :profiles {:uberjar {:aot :all}}
  :main clojars-json.core
  )
