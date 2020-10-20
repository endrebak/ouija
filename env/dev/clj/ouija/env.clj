(ns ouija.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [ouija.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[ouija started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[ouija has shut down successfully]=-"))
   :middleware wrap-dev})
