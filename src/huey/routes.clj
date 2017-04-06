(ns huey.routes
  (:require [cemerick.friend :as friend]
            [compojure.api.sweet :refer [GET api context middleware defroutes]]
            [hiccup.page :as h]
            [huey.auth :as auth]
            [huey.util :as u]
            [ring.util.response :as response]))

(defroutes user-routes
  (GET "/login" []
       (h/html5
         u/pretty-head
         (u/pretty-body
           [:h2 "Welcome to the Login!"])))

  (GET "/profile" []
       (h/html5
         u/pretty-head
         (u/pretty-body
           [:h2 "Welcome to the User Profile!"]))))

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
     :components components}

    (middleware
      [auth/wrap-auth]

      (GET "/" []
           (h/html5
             u/pretty-head
             (u/pretty-body
               [:h2 "Welcome to the Homepage!"])))

      (context
        "/healthcheck" []
        :tags ["Healthcheck"]

        (GET "/ping" []
             (-> (response/response "pong")
                 (response/content-type "text/plain; charset=UTF-8"))))

      (context
        "/auth" []
        :tags ["auth"]

        )

      (context
        "/user" []
        :tags ["user"]
        (friend/wrap-authorize user-routes #{::user})))))
