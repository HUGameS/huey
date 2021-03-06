(ns huey.auth
  (:require [cemerick.friend :as friend]
            [config.core :refer [env]]
            [friend-oauth2.util :as util]
            [friend-oauth2.workflow :as oauth2]
            [huey.user.handlers :as user]))

(def client-config
  {:client-id     (get-in env [:github-oauth2 :client-id])
   :client-secret (get-in env [:github-oauth2 :client-secret])
   :callback      (get-in env [:github-oauth2 :callback])})

(defn credential-fn
  [db token]
  (let [user (user/get-user-from-github (:access-token token))]
    (user/check-user-details db user)
    (-> (user/get-user-role-and-id db user)
        (assoc :identity token))))

(def uri-config
  {:authentication-uri {:url   (get-in env [:github-oauth2 :auth-url])
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (util/format-config-uri client-config)
                                :scope "email"}}

   :access-token-uri {:url   (get-in env [:github-oauth2 :access-uri])
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (util/format-config-uri client-config)}}})

(defn friend-config
  [db]
  {:allow-anon? true
   :workflows   [(oauth2/workflow {:client-config        client-config
                                   :uri-config           uri-config
                                   :access-token-parsefn util/get-access-token-from-params
                                   :credential-fn        (partial credential-fn db)})]})
