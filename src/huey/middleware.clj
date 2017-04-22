(ns huey.middleware
  (:require [clojure.java.jdbc :as jdbc]
            [cemerick.friend :as friend]
            [compojure.api.middleware :as compojure-api]
            [huey.auth :as auth]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn wrap-transaction
  [handler]
  (fn [req]
    (let [db (get-in req [::compojure-api/components :db])]
      (jdbc/with-db-transaction [tx db]
        (-> (assoc-in req [::compojure-api/components :db] tx)
            (handler))))))

(defn wrap-middleware
  [handler db]
  (-> handler
      wrap-transaction
      (friend/authenticate (auth/friend-config db))
      (wrap-defaults site-defaults)))
