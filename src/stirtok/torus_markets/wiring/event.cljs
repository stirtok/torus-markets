(ns stirtok.torus-markets.wiring.event
  "Re-frame 'events'.
   Declarative descriptions (just data) of the required side effects."
  (:require [re-frame.core :as rf]
            [stirtok.torus-markets.view.markets :as markets-view]))


(rf/reg-event-db
 :initialise-db
 (fn [_ _]
   {:route              {:data {:name :torus-markets.router/markets-view
                                :view markets-view/root}}
    :display-when-zero? false
    :status             {:ok? true
                         :msg ""}}))

(rf/reg-event-db
 :set-status
 (fn [db [_ v]]
   (assoc db :status v)))

(rf/reg-event-db
 :set-route
 (fn [db [_ v]]
   (tap> v)
   (assoc db :route v)))

(rf/reg-event-db
 :set-display-when-zero?
 (fn [db [_ v]]
   (tap> v)
   (assoc db :display-when-zero? v)))

(rf/reg-event-fx
 :fetch-markets
 (fn [cofx _]
   (let [db (:db cofx)]
     {:fetch-markets db
      :db            db})))

(rf/reg-event-db
 :set-markets
 (fn [db [_ m]]
   (assoc db :markets m)))