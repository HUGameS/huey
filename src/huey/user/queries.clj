(ns huey.user.queries
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "huey/user/sql/huey.sql")
