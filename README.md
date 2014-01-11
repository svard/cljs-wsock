# cljs-wsock

Clojurescript wrapper for goog.net.WebSocket.

A small project for teaching myself clojurescript and core.async library.

## Usage

```sh
lein install
```
and add
```clojure
[cljs-wsock "0.1.1-SNAPSHOT"]
```
as dependency in project.clj

Example

```clojure
(ns example
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-wsock.core :as ws]
            [cljs.core.async :refer [<!]]))

(defn websocket []
  (go
    (let [ch (ws/open "ws://localhost:8080")
          [status event] (<! ch)]
      (if (= :message status)
            (prn (str "Received message " (.-message event)))))))
```

## License

Copyright © 2014 Kristofer Svärd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
