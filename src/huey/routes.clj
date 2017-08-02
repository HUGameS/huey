(ns huey.routes
  (:require [cemerick.friend :as friend]
            [clojure.java.io :as io]
            [compojure.api.sweet :refer [ANY GET api context middleware defroutes]]
            [huey.auth :as auth]
            [huey.exceptions :as ex]
            [huey.middleware :as mw]
            [huey.user.routes :as user]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [schema.core :as s]))

(defn app
  [components]
  (api
    {:swagger
     {:ui "/docs"
      :spec "/swagger.json"
      :data {:info {:title "HUGameS Backend"
                    :description "Login, Matchmaking and Game Service"}
             :tags [{:name "auth" :description "User Authentication"}
                    {:name "match" :description "Game Matchmaking"}
                    {:name "game" :description "Game Lobbies"}
                    {:name "user" :description "User Details"}
                    {:name "Healthcheck" :description "Routes to determine the status of the service"}]}}
     :components components
     :exceptions ex/exceptions}


    (middleware
       [[wrap-defaults site-defaults]]
      (GET "/" [] (io/resource "repl.txt"))
      (context "/healthcheck" [] :tags ["Healthcheck"]
          (GET "/ping" []
               (-> (response/response "pong")
                   (response/content-type "text/plain; charset=UTF-8")))))

    (middleware
      [[mw/wrap-middleware (:db components)]]

      (context "/auth" [] :tags ["auth"]
        (GET "/login" [] (friend/authorize #{:huey.user.models/user} (response/redirect "/user/profile")))
        (friend/logout (ANY "/logout" request (response/redirect "/"))))

      (context "/user" [] :tags ["user"]
        user/user-routes))))
