(defproject cljs-wsock "0.4.0"
  :description "Clojurescript wrapper for goog.net.WebSocket"
  :url "https://github.com/svard/cljs-wsock"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.298.0-2a82a1-alpha"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
              :builds {
                       :dev {
                             :source-paths ["src"]
                             :compiler {
                                        :output-to "main.js"
                                        :optimizations :whitespace}}}})
