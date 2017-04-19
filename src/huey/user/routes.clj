(ns huey.user.routes
  (:require [cemerick.friend :as friend]
            [compojure.api.sweet :refer [ANY GET api context middleware defroutes]]
            [hiccup.page :as h]
            [huey.auth :as auth]
            [huey.exceptions :as ex]
            [huey.middleware :as mw]
            [huey.user.models :as m]
            [huey.util :as u]
            [ring.util.response :as response]
            [schema.core :as s]))

(defroutes user-routes
  (GET "/profile" []
       (friend/authorize #{:huey.user.models/user}
                         (h/html5
                           u/pretty-head
                           (u/pretty-body
                             [:h2 "Welcome to the User Profile!"]
                             [:p (str "<a href=\"/\">Home</a><br />"
                                      "<a href=\"/user/profile\">Profile</a><br />"
                                      "<a href=\"/status\">Status</a><br />"
                                      "<a href=\"/logout\">Log out</a>")])))))
