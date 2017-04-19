(ns huey.user.github
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
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

(defn get-user-role-and-id
 [db user]
 (let [role (keyword (:role (q/select-role-by-user-id db {:user_id (:user_id user)})))]
   {:roles    #{(get m/roles role)}
    :user_id  (:user_id user)}))

(defn update-user-details?
  [db {:keys [user_id updated_at]}]
  (let [current-updated-at (:updated_at (q/select-user-by-id db {:user_id user_id}))]
    (t/before? current-updated-at (c/from-string updated_at))))

(defn update-user-details!
  [db user]
  (->> (update user :updated_at c/to-sql-time)
       (q/update-user-details-by-id db)))

(defn check-user-details
 [db user]
 (if-not (existing-user? db (:user_id user))
   (insert-new-user! db user)
   (when (update-user-details? db user)
     (update-user-details! db user))))
