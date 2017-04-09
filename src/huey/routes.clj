(ns huey.routes
  (:require [cemerick.friend :as friend]
            [compojure.api.sweet :refer [ANY GET api context middleware defroutes]]
            [hiccup.page :as h]
            [huey.auth :as auth]
            [huey.util :as u]
            [ring.middleware.defaults :as ring-defaults]
            [ring.util.response :as response]
            [schema.core :as s]))

(defn wrap-middleware
  [handler]
  (-> handler
      (friend/authenticate auth/friend-config)
      (ring-defaults/wrap-defaults ring-defaults/site-defaults)))

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
      [wrap-middleware]
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

      (context
        "/healthcheck" []
        :tags ["Healthcheck"]

        (GET "/ping" []
             (-> (response/response "pong")
                 (response/content-type "text/plain; charset=UTF-8"))))
      (context
        "/user" []
        :tags ["user"]
        (GET "/profile" []
             (friend/authorize #{:huey.auth/user}
                               (h/html5
                                 u/pretty-head
                                 (u/pretty-body
                                   [:h2 "Welcome to the User Profile!"]
                                   [:p (str "<a href=\"/\">Home</a><br />"
                                            "<a href=\"/user/profile\">Profile</a><br />"
                                            "<a href=\"/status\">Status</a><br />"
                                            "<a href=\"/logout\">Log out</a>")]))))))))
