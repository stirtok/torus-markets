(ns stirtok.torus-markets.view.markets
  "The 'markets' view."
  (:require [cljs.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [re-frame.core :as rf]
            [testdouble.cljs.csv :as csv]
            [stirtok.torus-markets.util :as util]
            [stirtok.torus-markets.wiring.subscription :refer [listen]]))

(defn root
  []
  (let [{:keys [timestamp torus-markets]
         :as   markets-data} (listen [:markets])
        display-when-zero?                                  (listen [:display-when-zero?])
        headers                                             [["Token name" "left"]
                                                             ["Token addr" "left"] 
                                                             ["Mkt addr" "left"]
                                                             [[:span "Amt CVM" [:sup "1"] " in pool"] "right"] 
                                                             ["Amt token in pool" "right"]
                                                             #_[[:span "Price" [:sup "1"]] "right"] 
                                                             [[:span "Buy quote" [:sup "1"]] "center"] 
                                                             [[:span "Sell quote" [:sup "1"]] "right"]]
        cols                                                [[:token-name "left"] 
                                                             [:token-addr "right"] 
                                                             [:market-addr "right"]
                                                             [:pool-cvm "right"] 
                                                             [:pool-token "right"]
                                                             #_[:price "right"] 
                                                             [:buy-quote "right"] 
                                                             [:sell-quote "right"]]
        torus-markets                                       (sort-by #(js/parseInt (subs (:token-addr %) 1)) torus-markets)
        torus-markets                                       (if display-when-zero?
                                                              torus-markets
                                                              (filter #(> (:pool-cvm %) 0) torus-markets))
        tdata                                               (->> torus-markets
                                                                 (map (fn [m]
                                                                        (assoc m
                                                                               ;; Format the name as a tooltip.
                                                                               :token-name (let [m2 (:token-reg m)]
                                                                                             [:span.tooltip {:style        {:cursor "default"}
                                                                                                             :data-tooltip (or (:desc m2) (:description m2))} (:name m2)])
                                                                               ;; Format number values.
                                                                               :pool-cvm (let [x (:pool-cvm m)] (if (number? x) (util/fmt-int x) x))
                                                                               :pool-token (let [x (:pool-token m)] (if (number? x) (util/fmt-int x) x))
                                                                               :price (let [x (:price m)] (if (number? x) (util/fmt-dec x) x))
                                                                               :buy-quote (let [x (:buy-quote m)] (if (number? x) (util/fmt-dec x) x))
                                                                               :sell-quote (let [x (:sell-quote m)] (if (number? x) (util/fmt-dec x) x))))))
        timestamp                                           (util/fmt-date (js/Date. timestamp))
        edn-str                                             (with-out-str (pprint markets-data))
        csv-str                                             (str "data:text/csv;charset=utf-8,"
                                                                 (csv/write-csv (concat [["token-name" "token-desc" "token-addr" "market-addr" "pool-cvm" "pool-token" "price" "buy-quote" "sell-quote" "timestamp"]]
                                                                                        (map (fn [m]
                                                                                               [(:name (:token-reg m))
                                                                                                (or (:desc (:token-reg m)) (:description (:token-reg m)))
                                                                                                (:token-addr m)
                                                                                                (:market-addr m)
                                                                                                (:pool-cvm m)
                                                                                                (:pool-token m)
                                                                                                (:price m)
                                                                                                (:buy-quote m)
                                                                                                (:sell-quote m)
                                                                                                timestamp])
                                                                                             torus-markets))))]
    [:article 
     [:nav 
      [:h3 "Markets"]
      ""
      [:label
       [:input {:type      "checkbox"
                :id        "display-when-zero"
                :checked   display-when-zero?
                :value     (if display-when-zero? "on" "off")
                :on-change #(rf/dispatch [:set-display-when-zero? (.-checked (.-target %))])}]
       [:small " Display zero-amount markets?"]]]
     [:section
      [:table
       [:thead
        [:tr (for [[ix [header text-align]] (map-indexed vector headers)]
               [:th {:key   ix
                     :style {:text-align text-align}} header])]]
       [:tbody (for [[iy row-map] (map-indexed vector tdata)]
                 [:tr {:key iy}
                  (for [[ix [col-key text-align]] (map-indexed vector cols)]
                    [:td {:key   (str iy "_" ix)
                          :style {:text-align text-align}} (get row-map col-key)])])]]]
     [:p [:small [:sup "1"] "In Convex " [:em  "copper"] " (the utility token of the distributed ledger)" [:br] 
          "Convex timestamp: " timestamp [:br]
          "Download this data as: " 
          [:a {:href     (js/encodeURI (str "data:application/edn;charset=utf-8," edn-str))
               :download "torus-markets-data.edn"} "EDN"]
          " or "
          [:a {:href     (js/encodeURI csv-str)
               :download "torus-markets-data.csv"} "CSV"] [:br]
          "Refresh the page to fetch the latest data"]]]))