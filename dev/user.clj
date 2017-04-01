(ns user
  (:require [clojure.repl :refer :all]
            [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]
            [huey.system :as system]))

(reloaded.repl/set-init! #(system/new-system env))

(go)
