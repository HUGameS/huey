(ns huey.system
  (:require [com.stuartsierra.component :as component]
            [huey.routes :as r]
            [immutant.web :as web]
            [taoensso.timbre :as log])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

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

(defrecord ConnectionPool
  [config]
  component/Lifecycle
  (start [this]
    (assoc this :datasource
           (doto (ComboPooledDataSource.)
             (.setDriverClass (:classname config))
             (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
             (.setUser (:user config))
             (.setPassword (:password config))
             ;; expire excess connections after 30 minutes of inactivity:
             (.setMaxIdleTimeExcessConnections (* 30 60))
             ;; expire connections after 3 hours of inactivity:
             (.setMaxIdleTime (* 3 60 60)))))
  (stop [this]
    (when-let [datasource (:datasource this)]
      (.close datasource))
    (assoc this :datasource nil)))

(defn new-system
  [{:keys [server mysql]
    :as   config}]
  (-> (component/system-map
        :app    (->App r/app)
        :server (->ImmutantWebServer server)
        :db     (->ConnectionPool mysql))
      (component/system-using
        {:app    [:db]
         :server [:app]})))
