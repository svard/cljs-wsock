(ns cljs-wsock.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [chan put!]])
  (:import [goog.net WebSocket]
           [goog.net.WebSocket EventType]))

(def ^:private socket (WebSocket.))

(def ^:private channel (chan))

(defn ^:private listen
  "Sets up event listeners for the websocket connection"
  []
  (events/listen socket EventType/OPENED (fn [e]
                                           (put! channel [:opened e])))
  (events/listen socket EventType/MESSAGE (fn [e]
                                            (put! channel [:message e])))
  (events/listen socket EventType/CLOSED (fn [e]
                                           (put! channel [:closed e])))
  (events/listen socket EventType/ERROR (fn [e]
                                          (put! channel [:error e]))))

(defn open
  "Opens a connection to the specified url. Returns a channel that will
  receive a vector [type event] where type can be :opened, :message,
  :closed or :error

  url - a string specifying which server to connect to

  protocol - optional, string describing what subprotocol to use
  "
  ([url]
    (open url nil))
  ([url protocol]
    (listen)
    (.open socket url protocol)
    channel))

(defn send
  "Sends a message on an open web socket connection

  message - string to send
  "
  [message]
  (.send socket message))

(defn close
  "Close an open web socket connection"
  []
  (.close socket))

(defn open?
  "Checks to see if the web socket is open. Returns a boolean"
  []
  (.isOpen socket))
