(ns huey.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]))

(defn app
  [components]
  (routes
    (GET "/ping" []
         (-> (response/response "pong")
             (response/content-type "text/plain; charset=UTF-8")))
    (route/not-found "<h1>Page not found</h1>")))
