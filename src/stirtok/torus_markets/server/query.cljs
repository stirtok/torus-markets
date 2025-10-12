(ns stirtok.torus-markets.server.query
  "General user queries."
  (:require [re-frame.core :as rf]
            [stirtok.torus-markets.config :as config]
            [stirtok.torus-markets.util :as util]
            [stirtok.torus-markets.server.cvx :as cvx] 
            [stirtok.torus-markets.server.proxy :as convex]))


(defn- nicer
  "Prepend a # to addresses since the # syntax doesn't get through the REST machinations."
  [markets]
  {:timestamp     (:timestamp markets)
   :torus-markets (map #(assoc %
                               :token-addr (str "#" (:token-addr %))
                               :market-addr (str "#" (:market-addr %)))
                       (:torus-markets markets))})

(defn markets
  []
  (convex/query config/api
                config/addr
                cvx/markets
                (fn [m]
                  (if (contains? m :errorCode)
                    (rf/dispatch [:set-status (util/err-status m)])
                    (do (rf/dispatch [:set-status (util/ok-status "Received markets data")])
                        (rf/dispatch [:set-markets (nicer (:value m))]))))))

