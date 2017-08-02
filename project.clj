(defproject huey "0.1.0-SNAPSHOT"
  :description "HUGameS Backend - Login, Matchmaking and Game Service"
  :dependencies [[cheshire                   "5.7.0"]
                 [clj-http                   "2.3.0"]
                 [clj-time                   "0.13.0"]
                 [clojusc/friend-oauth2      "0.2.0"]
                 [com.cemerick/friend        "0.2.3" :exclusions [org.apache.httpcomponents/httpclient]]
                 [com.layerware/hugsql       "0.4.7"]
                 [com.mchange/c3p0           "0.9.2.1"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/sente         "1.11.0"]
                 [com.taoensso/timbre        "4.7.4"]
                 [environ                    "1.1.0"]
                 [metosin/compojure-api      "1.1.10"]
                 [mysql/mysql-connector-java "6.0.5"]
                 [org.clojure/clojure        "1.8.0"]
                 [org.clojure/core.async     "0.2.395"]
                 [org.clojure/java.jdbc      "0.7.0-alpha3"]
                 [org.immutant/web           "2.1.6"]
                 [ragtime                    "0.7.1"]
                 [ring/ring-core             "1.6.0-RC1"]
                 [ring/ring-defaults         "0.2.1"]
                 [yogthos/config             "0.8"]]
  :ring {:handler huey.routes/app}
  :main ^:skip-aot huey.core
  :repl-options {:init-ns user
                 :welcome (println (slurp (clojure.java.io/resource "repl.txt")))}
  :uberjar-name "huey-standalone.jar"
  :profiles {:prod {:resource-paths ["resources/prod"]}
             :dev  {:dependencies [[reloaded.repl "0.2.3"]
                                   [midje "1.8.3"]]
                    :source-paths ["dev"]
                    :resource-paths ["dev/resources"]
                    :plugins [[lein-environ "1.1.0"]
                              [lein-ring "0.10.0"]
                              [lein-midje "3.2.1"]]}
             :uberjar {:aot :all}})
