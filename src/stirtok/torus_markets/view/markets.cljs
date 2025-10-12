(ns stirtok.torus-markets.view.markets
  "The 'markets' view."
  (:require [cljs.pprint :refer [pprint]]
            [testdouble.cljs.csv :as csv]
            [stirtok.torus-markets.util :as util]
            [stirtok.torus-markets.wiring.subscription :refer [listen]]))

(defn root
  []
  (let [{:keys [timestamp torus-markets]
         :as   markets-data} (listen [:markets])
        cols                                                [[:token-name "left"] [:token-addr "right"] [:market-addr "right"]
                                                             [:pool-cvm "right"] [:pool-token "right"]
                                                             [:price "right"] [:buy-quote "right"] [:sell-quote "right"]]
        torus-markets                                       (sort-by :market-addr torus-markets)
        tdata                                               (->> torus-markets
                                                                 (map (fn [m]
                                                                        (assoc m
                                                                               ;; Format the name as a tooltip.
                                                                               :token-name (let [m2 (:token-reg m)]
                                                                                             [:span.tooltip {:data-tooltip (or (:desc m2) (:description m2))} (:name m2)])
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
     [:h3 "Markets"]
     [:section
      [:table
       [:thead
        [:tr (for [[k ta] cols]
               [:th {:style {:text-align ta}} (name k)])]]
       [:tbody (for [m tdata]
                 [:tr (for [[k ta] cols]
                        [:td {:style {:text-align ta}} (k m)])])]]]
     [:p [:small "Convex timestamp: " timestamp [:br]
          "Download this data as: " 
          [:a {:href     (js/encodeURI (str "data:application/edn;charset=utf-8," edn-str))
               :download "torus-markets-data.edn"} "EDN"]
          " or "
          [:a {:href     (js/encodeURI csv-str)
               :download "torus-markets-data.csv"} "CSV"] [:br]
          "Refresh the page to fetch the latest data"]]]))