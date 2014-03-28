(ns cljs-wsock.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [chan put!]]
            [cljs.reader :refer [read-string]])
  (:import [goog.net WebSocket]
           goog.net.WebSocket.EventType))

(def ^:private socket (atom nil))

(defn ^:private listen
  "Sets up event listeners for the websocket connection"
  [channel]
    (events/listen @socket goog.net.WebSocket.EventType.OPENED (fn []
                                           (put! channel [:opened])))
    (events/listen @socket goog.net.WebSocket.EventType.MESSAGE (fn [e]
                                            (put! channel [:message (read-string (.-message e))])))
    (events/listen @socket goog.net.WebSocket.EventType.CLOSED (fn []
                                           (put! channel [:closed])))
    (events/listen @socket goog.net.WebSocket.EventType.ERROR (fn []
                                          (put! channel [:error]))))

(defn open!
  "Opens a connection to the specified url. Returns a channel that will
  receive a vector [type event] where type can be :opened, :message,
  :closed or :error. In case of :message event will contain the received message.

  url - string, specifying which server to connect to

  protocol - string, optional, describing what subprotocol to use

  auto-reconnect - boolean, optional, should the web socket automatically try to reconnect.
                   True by default.

  get-next-reconnect - number or fn, optional, function for obtaining the time until next
                       reconnect attempt. fn will be passed the reconnect attempt
                       count and should return a positive integer representing the
                       time in milliseconds until next reconnect attempt.
  "
  ([url]
   (open! url nil))
  ([url protocol]
   (open! url protocol nil))
  ([url protocol auto-reconnect]
   (open! url protocol auto-reconnect nil))
  ([url protocol auto-reconnect get-next-reconnect]
   (let [channel (chan)]
     (reset! socket (WebSocket. auto-reconnect get-next-reconnect))
     (listen channel)
     (.open @socket url protocol)
     channel)))

(defn send
  "Sends a message on an open web socket connection

  message - message to send, will be converted to string
  "
  [message]
  (when @socket
    (if (= (type message) js/String)
      (.send @socket message)
      (.send @socket (pr-str message)))))

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
