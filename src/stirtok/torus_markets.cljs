(ns stirtok.torus-markets
  "This is the Torus Markets app.
   It is implemented as a single (web)page app 
   written in ClojureScript that expects to be run
   within a webpage inside a web browser.
   It is over engineered for what it does with the idea of 
   providing a structure for future app development."
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf] 
            [stirtok.torus-markets.router :as router] 
            [stirtok.torus-markets.navbar :as navbar]
            [stirtok.torus-markets.wiring.event]
            [stirtok.torus-markets.wiring.effect]
            [stirtok.torus-markets.wiring.subscription :refer [listen]]))



(defn root
  []
  [:div
   [:header [navbar/root]]
   [:main (let [route (listen [:route])
                view  (-> route :data :view)]
            (when view 
              [view]))]])


;; Called by init and after code reloading finishes.
(defn ^:dev/after-load start
  []
  (js/console.log "Starting Torus Markets app")
  (js/console.log "Starting router")
  (router/init)
  (js/console.log "Starting render")
  (rdom/render [root] (do
                        (.addEventListener (first (.getElementsByTagName js/document "body")) 
                                           "click" 
                                           #(rf/dispatch [:set-status {:ok? true
                                                                       :msg ""}]))
                        (js/console.log "Initialising db")
                        (rf/dispatch [:initialise-db])
                        (rf/dispatch [:fetch-markets])
                        (.getElementById js/document "app"))))

;; `init` is called ONCE when the page loads.
;; This is called from the HTML file and must be exported
;; so it is available even in :advanced release builds
(defn ^:export init
  []
  (start))

;; This is called before any code is reloaded.
(defn ^:dev/before-load stop
  []
  (js/console.log "Stopping Torus Markets app"))