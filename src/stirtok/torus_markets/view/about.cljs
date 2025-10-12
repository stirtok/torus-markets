(ns stirtok.torus-markets.view.about
  "The 'about' view."
  (:require [stirtok.torus-markets.config :as config]))

(defn root
  []
  [:article 
   
   [:h3 "About"]
   [:dl
    
    [:dt "Convex API URL"] 
    [:dd [:b config/api]]
    
    [:dt "Purpose of this web app"]
    [:dd [:span "This app displays data about the Torus markets data that is on the Convex distributed ledger." [:br]
          "Its purpose is to be a learning example and a starting point for future app development."]]
    
    [:dt "Author of this web app"] 
    [:dd "The "
     [:a {:href   config/stirtok
          :target "_blank"}
      [:span {:style {:display     "flex"
                      :align-items "center"}}
       [:img {:alt    "StirTok logo"
              :src    "img/stirtok-logo.png"
              :height "15"}]
       [:span \u00A0]
       "StirTok"]] " project"]]
   
   [:h3 "Related"]
   [:dl
    
    [:dt "Convex website"]
    [:dd [:a {:href   config/website
              :target "_blank"} config/website]]
    
    [:dt "Convex documentation"]
    [:dd [:a {:href   config/docs
              :target "_blank"} config/docs]]
    
    [:dt "Quick-lookup reference documentation for Convex’s standard libraries"]
    [:dd [:a {:href   config/quick-ref
              :target "_blank"} config/quick-ref]]]])