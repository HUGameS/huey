(ns huey.user.github
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.coerce :as c]
            [clj-time.jdbc]
            [clojure.set :as set]
            [huey.user.models :as m]
            [huey.user.queries :as q]
            [schema.core :as s]))

(defn get-user-from-github
  [access-token]
  (let [github-response (client/get "https://api.github.com/user"
                                    {:query-params {:access_token access-token}})
        user-info       (parse-string (:body github-response) true)]
    (-> (set/rename-keys user-info {:id :user_id})
        (select-keys (keys m/github-user-model))
        (update :updated_at c/to-sql-time))))

(defn existing-user?
  [db user_id]
  (q/select-user-by-id db {:user_id user_id}))

(defn insert-new-user!
  [db user]
  (q/insert-new-user db user)
  (q/insert-user-role db {:user_id (:user_id user) :role "user"}))

(defn get-user-role
 [db user_id]
 (q/select-role-by-user-id db {:user_id user_id}))
