(ns ^:figwheel-always om-next-04.core
  (:import [goog.net XhrIo])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.pprint]
    [goog.dom :as gdom]
    [cognitect.transit :as tt]
    [om.next :as om :refer-macros [defui]]
    [om.dom :as dom]
    [clojure.test.check :as ck]
    [clojure.test.check.generators :as ckgs]
    [clojure.test.check.properties :as ckps]
    [cljs.core.async :refer [chan put!]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(def temp-id (om/tempid))

(defn reconciler-send []
  "makes a query to the remote and the result takes a callback to receive json response."
  (fn [re cb]
    (println re)
    (cb [['ids {:tempids {[:id temp-id] [:id 109]}}]])))

(defui Todo
       static om/Ident
       (ident [this {:keys [id]}]
              [:id id])
       static om/IQuery
       (query [this]
              [:id :title :completed :category])
       Object
       (render [this]
               (let [{:keys [id title completed category]} (om/props this)]
                 (dom/div nil
                          (dom/h3 nil (str title " : (" id ")"))
                          (when completed
                            (dom/label nil "Done!"))
                          (when-not completed
                            (dom/button #js {:onClick
                                             (fn [_] (om/transact! this `[(todos/complete [:id ~id])]))}
                                        "Do it!"))
                          (dom/label nil "Category:") (dom/span nil category)))))

(def todo (om/factory Todo {:keyfn :id}))

(defui Todos
       static om/IQuery
       (query [this]
              [{:todos (om/get-query Todo)}])
       Object
       (render [this]
               (let [{:keys [todos]} (om/props this)]
                 (apply dom/div nil
                        (map todo todos)))))

(def temp-init-data
  {:todos [{:id        99
            :title     "Get food"
            :completed true
            :category  "item"}
           {:id        100
            :title     "Get drink"
            :completed true
            :category  "item"}
           {:id        temp-id
            :title     "Make dinner"
            :completed false
            :category  "task"}]})

(defmulti reading om/dispatch)

(defmethod reading :todos
  [{:keys [ast path parser query state] :as env} key target]       ;; parsing is a function that recieves env[ast path parser query state] key target
  (let [st @state]
    {:value (om/db->tree query (get st key) st)}))

(defmulti mutating om/dispatch)

(defmethod mutating 'todos/complete
  [{:keys [ast path parser query state] :as env} key target]       ;; parsing is a function that recieves env[ast path parser query state] key target
  {:remote true
   :action
    (fn [])})

(def reconciler
  (om/reconciler
    {:state  temp-init-data                                 ;; 'raw' init data will be normalized to db type
     :parser (om/parser {:read reading :mutate mutating})
     :send   (reconciler-send)
     :id-key :id}))

(om/add-root! reconciler Todos (gdom/getElement "ui"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )