(ns huey.auth
  (:require [cemerick.friend :as friend]
            [config.core :refer [env]]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri]]))

(def client-config
  {:client-id     (get-in env [:oauth2 :client-id])
   :client-secret (get-in env [:oauth2 :client-secret])
   :callback      {:domain "http://localhost:3000" ;; replace this for production with the appropriate site URL
                   :path "/user/profile"}})

(defn credential-fn
  "Upon successful authentication with the third party, Friend calls
  this function with the user's token. This function is responsible for
  translating that into a Friend identity map with at least the :identity
  and :roles keys. How you decide what roles to grant users is up to you;
  you could e.g. look them up in a database.

  You can also return nil here if you decide that the token provided
  is invalid. This could be used to implement e.g. banning users.

  This example code just automatically assigns anyone who has
  authenticated with the third party the nominal role of ::user."
  [token]
    {:identity token
     :roles #{::user}})

(def uri-config
  {:authentication-uri {:url "https://accounts.google.com/o/oauth2/auth"
                        :query {:client_id (:client-id client-config)
                               :response_type "code"
                               :redirect_uri (format-config-uri client-config)
                               :scope "email"}}

   :access-token-uri {:url "https://accounts.google.com/o/oauth2/token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri client-config)}}})

(def friend-config
  {:allow-anon? true
   :workflows   [(oauth2/workflow
                  {:client-config client-config
                   :uri-config uri-config
                   :credential-fn credential-fn})
                   ;; Optionally add other workflows here...
                   ]})

(defn wrap-auth
  [handler]
  (-> handler
      (friend/authenticate friend-config)))
