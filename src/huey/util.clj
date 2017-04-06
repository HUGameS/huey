(ns huey.util)

(def pretty-head
  [:head [:link {:href "/css/normalize.css" :rel "stylesheet" :type "text/css"}]
         [:link {:href "/css/foundation.min.css" :rel "stylesheet" :type "text/css"}]
         [:style {:type "text/css"} "ul { padding-left: 2em }"]
         [:script {:src "/js/foundation.min.js" :type "text/javascript"}]])

(defn pretty-body
  [& content]
  [:body {:class "row"}
   (into [:div {:class "columns small-12"}] content)])
