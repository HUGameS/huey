(ns huey.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [huey.routes :as r]
            [immutant.web :as web]
            [taoensso.timbre :as log]))

(defrecord App
  [handler-fn]
  component/Lifecycle
  (start [this]
    (assoc this :handler (handler-fn this)))
  (stop [this]
    (assoc this :handler nil)))

(defrecord ImmutantWebServer
  [config]
  component/Lifecycle
  (start [this]
    (let [handler (get-in this [:app :handler :handler])]
      (assoc this :server (web/run handler config))))
  (stop [this]
    (when-let [server (:server this)]
      (web/stop server))
    (assoc this :server nil)))

(defn new-system
  [{:keys [server]
    :as   config}]
  (-> (component/system-map
        :app       (->App r/app)
        :server    (->ImmutantWebServer server))
      (component/system-using
        {:server [:app]})))
