(ns huey.user.models
  (:require [schema.core :as s]))

(def roles
  {:user  ::user
   :admin ::admin})

(s/defschema github-user-model
 {:user_id    s/Int
  :login      s/Str
  :name       s/Str
  :bio        (s/maybe s/Str)
  :email      s/Str
  :company    s/Str
  :location   s/Str
  :html_url   s/Str
  :updated_at s/Inst})
