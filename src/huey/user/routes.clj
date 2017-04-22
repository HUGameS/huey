(ns huey.user.routes
  (:require [cemerick.friend :as friend]
            [compojure.api.sweet :refer [ANY GET api context middleware defroutes]]
            [huey.auth :as auth]
            [huey.exceptions :as ex]
            [huey.middleware :as mw]
            [huey.user.handlers :as h]
            [huey.user.queries :as q]
            [huey.user.models :as m]
            [ring.util.response :as response]
            [schema.core :as s]))

(defroutes user-routes
  (GET "/profile" request
       :components [db]
       (friend/authorize #{:huey.user.models/user}
         (-> (h/select-user-by-id db request)
             response/response))))
