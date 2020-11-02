(ns ouija.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [ouija.ajax :as ajax]
    [ouija.events]
    [ouija.highlight :refer [highlight]]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string]
    [com.rpl.specter :refer [MAP-VALS]])
  (:import goog.History))


(js/console.log ouija.highlight)

                ;; /highlight-select [MAP-VALS] [{:a 1}])) 
(js/console.log ouija.events) 

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "ouija"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])



;; (defn specter-form [query]
;;   (let [fields (r/atom {:query ""})]
;;     (fn []
;;       [:div
;;        [:div.field
;;         [:label.label {:for :name} "Name"]
;;         [:input.input {:type :text
;;                        :name :name
;;                        :on-change #(swap! fields assoc)}
;; 
;; (let [ready? (rf/subscribe [:initialized?])]
;;      (if @ready?
;;        (do (js/console.log "ready") [message-form])
;;        (do (js/console.log "not ready") [:h1 "Initializing"])))

(defn message-form []
  (let [ready? (rf/subscribe [:initialized?])
        fields (r/atom {})]
    (fn []
      (if @ready?
        [:div
         [:div.field
          [:label.label {:for :name} "Specter path"]
          [:input.input
           {:type :text
            :name :path
                                        ; problem: had nested let!
            :on-change #(let [path (-> % .-target .-value)]
                          (swap! fields assoc :path path)
                          (rf/dispatch [:fields/path path]))
            :value (:path @fields)
            }]]
         [:div.field
          [:label.label {:for :name} "Structure"]
          [:input.input
           {:type :text
            :name :structure
                                        ; problem: had nested let!
            :on-change #(let [structure (-> % .-target .-value)]
                          (swap! fields assoc :structure structure)
                          (rf/dispatch [:fields/structure structure]))
            :value (:structure @fields)
            }]]
         [:div [:p @(rf/subscribe [:highlight/result])]]]
        [:h1 "Initializing"]))))


(defn home-page []
  [:section.section>div.container>div.content
   [:h1 "hello"]
   [message-form]])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rf/dispatch [:initialize-db])
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
