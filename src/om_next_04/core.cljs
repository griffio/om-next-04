(ns ^:figwheel-always om-next-04.core
  (:import [goog.net XhrIo])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.pprint]
    [goog.dom :as gdom]
    [cognitect.transit :as tt]
    [om.next :as om :refer-macros [defui]]
    [om.dom :as dom]
    [cljs.core.async :refer [chan put!]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn reconciler-send []
  "Simulated remote that maps the local temp-id to remote id"
  (fn [remotes callback]
    ;;{:remote [(todos/complete [:id #om/id["a2ef6b16-5b06-4d36-9031-188ebdc3fd14"]])]}
    (let [{:keys [remote]} remotes
          {[children] :children} (om.next/query->ast remote)
          ;;{:type :root, :children [{:dispatch-key todos/complete, :key todos/complete, :params {:id #om/id["e45bb8ea-b861-45bf-bb6b-45eb7e919795"]}, :type :call}]}
          temp-id (get-in children [:params :id])]
      (callback [['todos/complete {:tempids {[:id temp-id] [:id 101]}}]])))) ;; return id for merge - must be in this format with 'symbol at the front

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
                          (if completed
                            (dom/label nil "Done!")
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
                        (dom/h2 nil "Todo")
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
           {:id        (om/tempid)                          ;;  e.g #om/id["298c278b-154b-4608-bf5e-9e70b42fc062"]
            :title     "Make dinner"
            :completed false
            :category  "task"}]})

(defmulti reading om/dispatch)

(defmethod reading :todos
  [{:keys [ast path parser query state] :as env} key target] ;; parsing is a function that receives env[ast path parser query state] key target
  (let [st @state]
    {:value (om/db->tree query (get st key) st)}))          ;; de-normalize, query is [:id :title :completed :category], key is :todos

(defmulti mutating om/dispatch)

(defmethod mutating 'todos/complete
  [{:keys [ast path parser query state] :as env} key target] ;; parsing is a function that receives env[ast path parser query state] key target
  {:remote true
   :action (fn []
             (letfn [(step [state' ref]
                       (update-in state' ref assoc          ;; set all to true
                                  :completed true))]
               (swap! state
                      #(reduce step % (:todos %)))))})

;; uses default-merge behaviour
(def reconciler
  (om/reconciler
    {:state  temp-init-data                                 ;; 'raw' init data will be normalized to db type
     :parser (om/parser {:read reading :mutate mutating})
     :send   (reconciler-send)
     :id-key :id}))                                         ;; used by temp-ids merge behaviour

(om/add-root! reconciler Todos (gdom/getElement "ui"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )