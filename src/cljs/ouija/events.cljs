(ns ouija.events
  (:require
    [re-frame.core :as rf]
    [clojure.edn :as edn]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [ouija.highlight :refer [highlight]]
    [com.rpl.specter :refer [MAP-VALS]])
  )

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
  :fields/path
  (fn [db [_ path]]
    ;; replace with resolve if you encounter a symbol
    (assoc db :path (eval (edn/read-string path)))))

(rf/reg-event-db
  :fields/structure
  (fn [db [_ structure]]
    (assoc db :structure (edn/read-string structure))))


(rf/reg-sub
 :fields/path
 (fn [db _]
 ;  (js/console.log (str "Hello from sub: " (:path db)))
   (:path db)))

(rf/reg-sub
 :fields/structure
 ;(js/console.log (str "Hello from sub: " (:structure db)))
 (fn [db _]
   (:structure db)))

(rf/reg-sub
 :result
 (fn [query-v]
   (js/console.log "Are we ever here?")
   [(rf/subscribe [:fields/path]) (rf/subscribe [:fields/structure])])
 (fn [[fields structure] query-v]
   (js/console.log (str "Hi from reg-sub: " fields structure))
   (str (highlight fields structure))))


(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

;;subscriptions

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
