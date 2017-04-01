(defproject huey "0.1.0-SNAPSHOT"
  :description "HUGameS Backend - Login, Matchmaking and Game Service"
  :dependencies [[com.stuartsierra/component "0.3.2"]
                 [com.taoensso/sente         "1.11.0"]
                 [com.taoensso/timbre        "4.7.4"]
                 [metosin/compojure-api      "1.1.10"]
                 [environ                    "1.1.0"]
                 [org.clojure/clojure        "1.8.0"]
                 [org.clojure/core.async     "0.2.395"]
                 [org.immutant/web           "2.1.6"]
                 [ring/ring-core             "1.6.0-RC1"]
                 [ring/ring-defaults         "0.2.1"]
                 [yogthos/config             "0.8"]]
  :ring {:handler huey.routes/app}
  :main ^:skip-aot huey.core
  :repl-options {:init-ns user}
  :uberjar-name "server.jar"
  :profiles {:prod {:resource-paths ["resources/prod"]}
             :dev  {:dependencies [[reloaded.repl "0.2.3"]
                                   [midje "1.8.3"]]
                    :source-paths ["dev"]
                    :resource-paths ["dev/resources"]
                    :plugins [[lein-environ "1.1.0"]
                              [lein-ring "0.10.0"]
                              [lein-midje "3.2.1"]]}
             :uberjar {:aot :all}})
