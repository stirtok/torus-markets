(ns stirtok.torus-markets.router
  (:require [re-frame.core :as rf] 
            [reitit.frontend :as rouf]
            [reitit.frontend.easy :as roue]
            [reitit.coercion :as rouc]
            [stirtok.torus-markets.view.markets :as markets-view]
            [stirtok.torus-markets.view.about :as about-view]))

(def routes
  [["/"
    {:name ::markets-view
     :view markets-view/root}]
   ["/about"
    {:name ::about-view
     :view about-view/root}]
   ["*path"
    {:name ::catch-all
     :view markets-view/root}]])

(defn init
  []
  (roue/start!
   (rouf/router routes
                {:compile   rouc/compile-request-coercers
                 :conflicts nil})
   (fn [m] (rf/dispatch [:set-route m]))
   {:use-fragment true})) ; So URLs looking like  base-path/?#/other/paths