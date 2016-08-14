(defproject om-next-04 "0.1.0-SNAPSHOT"
  :description "om-next-04"
  :url "https://github.com/griffio/om-next-04"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.89" :scope "provided"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "1.0.0-alpha41"]
                 [figwheel-sidecar "0.5.4-3" :scope "test"]]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
              :builds [{:id           "dev"
                        :source-paths ["src"]

                        :figwheel     {:on-jsload "om-next-04.core/on-js-reload"}

                        :compiler     {:main                 om-next-04.core
                                       :asset-path           "js/compiled/out"
                                       :output-to            "resources/public/js/compiled/om_next_04.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true}}
                       {:id           "min"
                        :source-paths ["src"]
                        :compiler     {:output-to     "resources/public/js/compiled/om_next_04.js"
                                       :main          om-next-04.core
                                       :optimizations :advanced
                                       :pretty-print  false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources" 
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1" 

             :css-dirs ["resources/public/css"]})             ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log" 

