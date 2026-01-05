(ns stirtok.torus-markets.wiring.subscription
  "Re-frame 'subscriptions'."
  (:require [re-frame.core :as rf]))


(rf/reg-sub :status (fn [db] (:status db)))
(rf/reg-sub :route (fn [db] (:route db)))
(rf/reg-sub :markets (fn [db] (:markets db)))
(rf/reg-sub :display-when-zero? (fn [db] (:display-when-zero? db)))


(defn listen
  [query-v]
  @(rf/subscribe query-v))


