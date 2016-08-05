(ns clojars-json.core
  (:require [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:import java.util.zip.GZIPInputStream)
  (:gen-class))

(defn fetch-url [url options]
  (let [stream-fn (if (:gzip options)
                    (fn [url]
                      (GZIPInputStream. (io/input-stream url)))
                    io/input-stream)]
    (with-open [in (stream-fn url)]
      (slurp in))))


(defn stringify-first [[f s]]
  [(str f) s])

(defn kv-to-hash [seq]
  (reduce (fn [acc [k v]]
            (let [current (get acc k [])]
              (assoc acc k (conj current v))))
          {}
          seq))

(defn get-package-versions [package-data]
  (kv-to-hash
   (map (fn [line]
          (-> line
              edn/read-string
              stringify-first))
        (clojure.string/split-lines package-data))))

(def package-url "https://clojars.org/repo/all-jars.clj.gz")
(def feed-url "http://clojars.org/repo/feed.clj.gz")

(defn process-url
  ([f url]
     (process-url f url {}))
  ([f url options]
     (f (fetch-url url options))))

(defn parse-feed [data]
  (kv-to-hash
   (map (fn [line]
          (let [m (edn/read-string line)]
            [(:artifact-id m) (dissoc m :artifact-id)]))
        (clojure.string/split-lines data))))

;; (def packages (process-url get-package-versions package-url))
;; (def feed (process-url parse-feed feed-url {:gzip true}))

(defn json-response [s]
  {:headers {"Content-Type" "application/json"}
   :body (json/encode s)})

(defroutes app-routes
  (GET "/" [] "<h1>JSON Clojars</h1>")
  (GET "/feed.json" []
       (json-response (process-url parse-feed feed-url {:gzip true})))
  (GET "/packages.json" []
       (json-response (process-url get-package-versions package-url {:gzip true})))
  (POST "/project.clj" []
        (fn [req]
          (json-response
            (-> req
                (:body)
                (slurp)
                (read-string)))))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (wrap-defaults #'app-routes (-> site-defaults
                                  (assoc :security {:anti-forgery false})
                                  (assoc :params false))))

(defn -main []
   (let [port (Integer. (or (System/getenv "PORT") "8080"))]
     (ring/run-jetty app  {:port port
                           :join? false})))
