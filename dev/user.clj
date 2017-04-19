(ns user
  (:require [clojure.repl :refer :all]
            [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [huey.system :as system]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [reloaded.repl :refer [system init start stop go reset reset-all]]))

(reloaded.repl/set-init! #(system/new-system env))

(def db-spec
  (let [{:keys [driver-class subprotocol subname user password]} (:mysql env)]
    {:classname   driver-class
     :subprotocol subprotocol
     :subname     subname
     :user        user
     :password    password}))

(def config
  {:datastore  (jdbc/sql-database db-spec)
   :migrations (jdbc/load-resources "migrations")})


(defn migrate
 []
 (repl/migrate config))

(defn rollback
 []
 (repl/rollback config))
