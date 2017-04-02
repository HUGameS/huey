(ns huey.routes
  (:require [compojure.api.sweet :refer [GET api context middleware]]
            [ring.util.response :as response]))

(defn app
  [components]
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "HUGameS Backend"
                    :description "Login, Matchmaking and Game Service"}
             :tags [{:name "auth", :description "User Authentication"}
                    {:name "match", :description "Game Matchmaking"}
                    {:name "game", :description "Game Lobbies"}
                    {:name "Healthcheck", :description "Routes to determine the status of the service"}]}}
     :components components}

    (context
      "/healthcheck" []
      :tags ["Healthcheck"]

      (GET "/ping" []
           (-> (response/response "pong")
               (response/content-type "text/plain; charset=UTF-8"))))))
