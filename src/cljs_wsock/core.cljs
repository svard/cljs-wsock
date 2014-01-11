(ns cljs-wsock.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [chan put!]])
  (:import [goog.net WebSocket]
           [goog.net.WebSocket EventType]))

(def ^:private socket (atom nil))

(def ^:private channel (chan))

(defn ^:private listen
  "Sets up event listeners for the websocket connection"
  []
  (events/listen @socket EventType/OPENED (fn [e]
                                           (put! channel [:opened e])))
  (events/listen @socket EventType/MESSAGE (fn [e]
                                            (put! channel [:message e])))
  (events/listen @socket EventType/CLOSED (fn [e]
                                           (put! channel [:closed e])))
  (events/listen @socket EventType/ERROR (fn [e]
                                          (put! channel [:error e]))))

(defn open
  "Opens a connection to the specified url. Returns a channel that will
  receive a vector [type event] where type can be :opened, :message,
  :closed or :error

  url - string, specifying which server to connect to

  protocol - string, optional, describing what subprotocol to use
  "
  ([url]
   (open url nil))
  ([url protocol]
   (open url protocol nil))
  ([url protocol auto-reconnect]
   (open url protocol auto-reconnect nil))
  ([url protocol auto-reconnect get-next-reconnect]
   (reset! socket (WebSocket. auto-reconnect get-next-reconnect))
   (listen)
   (.open @socket url protocol)
   channel))

(defn send
  "Sends a message on an open web socket connection

  message - string, message to send
  "
  [message]
  (when @socket
    (.send @socket message)))

(defn close
  "Close an open web socket connection"
  []
  (when @socket
    (.close @socket)))

(defn open?
  "Checks to see if the web socket is open. Returns a boolean"
  []
  (when @socket
    (.isOpen @socket)))
