(ns huey.auth
  (:require [cemerick.friend :as friend]
            [config.core :refer [env]]
            [friend-oauth2.util :as util]
            [friend-oauth2.workflow :as oauth2]
            [huey.user.github :as git]))

(def roles
  {:user  ::user
   :admin ::admin})

(def client-config
  {:client-id     (get-in env [:github-oauth2 :client-id])
   :client-secret (get-in env [:github-oauth2 :client-secret])
   :callback      {:domain "http://localhost:3000" ;; replace this for production with the appropriate site URL
                   :path "/oauthcallback"}})

(defn credential-fn
  [db token]
  (let [user (git/get-user-from-github (:access-token token))]
    (when-not (git/existing-user? db (:user_id user))
      (git/insert-new-user! db user))
    (let [role (keyword (:role (git/get-user-role db (:user_id user))))]
      {:identity token
       :roles #{(get roles role)}
       :user_id (:user_id user)})))

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
