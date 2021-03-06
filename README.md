# cljs-wsock

Clojurescript async wrapper for goog.net.WebSocket.

A small project for teaching myself clojurescript and core.async library.

## Usage

[![Clojars Project](http://clojars.org/cljs-wsock/latest-version.svg)](http://clojars.org/cljs-wsock)

## Example

```clojure
(ns example
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-wsock.core :as ws]
            [cljs.core.async :refer [<!]]))

(defn websocket []
  (go
    (let [ch (ws/open! "ws://localhost:8080")
          [status event] (<! ch)]
      (case status
        :opened (prn "Connection opened")
        :message (prn (str "Received message " event))
        :closed (prn "Connection closed")
        :error (prn "Error")))))
```

## License

Copyright © 2014 Kristofer Svärd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
