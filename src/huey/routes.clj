(ns huey.routes
  (:require [cemerick.friend :as friend]
            [compojure.api.sweet :refer [ANY GET api context middleware defroutes]]
            [hiccup.page :as h]
            [huey.auth :as auth]
            [huey.exceptions :as ex]
            [huey.middleware :as mw]
            [huey.user.routes :as user]
            [huey.util :as u]
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
      [[mw/wrap-middleware (:db components)]]
      (GET "/" []
           (h/html5
             u/pretty-head
             (u/pretty-body
               [:h2 "Welcome to the Homepage!"]
               [:p (str "<a href=\"/user/profile\">Profile</a><br />"
                        "<a href=\"/status\">Status</a><br />"
                        "<a href=\"/logout\">Log out</a>")])))
      (GET "/user-login" []
           (h/html5
             u/pretty-head
             (u/pretty-body
               [:h2 "Welcome to the Login!"])))
      (friend/logout (ANY "/logout" request (response/redirect "/")))

      (GET "/status" request
           (fn [request]
             (let [count (:count (:session request) 0)
                   session (assoc (:session request) :count (inc count))]
               (-> (str "<p>We've hit the session page "
                        (:count session)
                        " times.</p><p>The current session: "
                        session
                        "</p>"
                        "<a href=\"/\">Home</a><br />"
                        "<a href=\"/user/profile\">Profile</a><br />"
                        "<a href=\"/status\">Status</a><br />"
                        "<a href=\"/logout\">Log out</a>")
                   (response/response)
                   (response/content-type "text/html")))))

      (context "/user" [] :tags ["user"] user/user-routes)

      (context
        "/healthcheck" []
        :tags ["Healthcheck"]

        (GET "/ping" []
             (-> (response/response "pong")
                 (response/content-type "text/plain; charset=UTF-8")))))))
