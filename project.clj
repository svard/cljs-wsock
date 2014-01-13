(defproject cljs-wsock "0.1.2"
  :description "Clojurescript wrapper for goog.net.WebSocket"
  :url "https://github.com/svard/cljs-wsock"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild {
              :builds [{
                         :id "dev"
                         :source-paths ["src"]
                         :compiler {
                                    :output-to "main.js"
                                    :output-dir "out"
                                    :optimizations :none
                                    :source-map true}}]})
