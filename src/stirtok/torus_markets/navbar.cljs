(ns stirtok.torus-markets.navbar
  (:require [reitit.frontend.easy :as rfe]
            [stirtok.torus-markets.util :as util]
            [stirtok.torus-markets.wiring.subscription :refer [listen]]))


(defn- nav-item
  [current name label]
  (if (= current name)
    [:span {:style {:color "grey"}} label]
    [:a {:href (rfe/href name)} label]))

(defn- title
  []
  [:nav
   [:img {:alt    "Logo"
          :src    "img/convex-logo.png"
          :height "30"}]
   [:h3 "Convex Torus Markets"]
   [:ul [:li "Menu ▼" 
         (let [current (-> (listen [:route]) :data :name)]
           [:ul.dropleft {:style {:bgcolor "white"}}
            [:li (nav-item current :stirtok.torus-markets.router/markets-view "Markets")]
            [:li (nav-item current :stirtok.torus-markets.router/about-view "About")]])]]])

(defn- notification
  []
  (let [status            (listen [:status])
        {:keys [ok? msg]} status
        decorator         (if ok? util/green util/red)]
    [:small
     [:nav
      [:span \u00A0]
      [:span]
      [:span (decorator msg)]]]))

(defn root
  []
  [:div
   [title]
   [notification]])