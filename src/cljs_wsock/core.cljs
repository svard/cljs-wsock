(ns cljs-wsock.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [chan put!]]
            [cljs.reader :refer [read-string]])
  (:import [goog.net WebSocket]
           goog.net.WebSocket.EventType))

(def ^:private websockets (atom {}))

(defn ^:private listen
  "Sets up event listeners for the websocket connection."
  [channel]
    (let [websocket (@websockets channel)]
      (events/listen websocket
                     goog.net.WebSocket.EventType.OPENED
                     (fn []
                       (put! channel [:opened])))
      (events/listen websocket
                     goog.net.WebSocket.EventType.MESSAGE
                     (fn [e]
                       (put! channel [:message (read-string (.-message e))])))
      (events/listen websocket
                     goog.net.WebSocket.EventType.CLOSED
                     (fn []
                       (put! channel [:closed])))
      (events/listen websocket
                     goog.net.WebSocket.EventType.ERROR
                     (fn []
                       (put! channel [:error])))))

(defn open!
  "Opens a connection to the specified url. Returns a channel that will
  receive a vector [type event] where type can be :opened, :message,
  :closed or :error. In case of :message event will contain the received message.

  url - string, specifying which server to connect to.

  protocol - string, optional, describing what subprotocol to use.

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
   (let [channel (chan)
         websocket (WebSocket. auto-reconnect get-next-reconnect)]
     (swap! websockets assoc channel websocket)
     (listen channel)
     (.open websocket url protocol)
     channel)))

(defn send
  "Sends a message on the web socket connection associated with the channel.

  channel - channel associated with the web socket connection.
  message - message to send, will be converted to string.
  "
  [channel message]
  (when-let [websocket (@websockets channel)]
    (if (= (type message) js/String)
      (.send websocket message)
      (.send websocket (pr-str message)))))

(defn close!
  "Close the web socket connection associated with the channel.

  channel - channel associated with the web socket to close.
  "
  [channel]
  (when-let [websocket (@websockets channel)]
    (swap! websockets dissoc channel)
    (.close websocket)))

(defn open?
  "Checks to see if the web socket associated with channel is open. Returns a boolean.

  channel - channel associated with the web socket to check.
  "
  [channel]
  (if-let [websocket (@websockets channel)]
    (.isOpen websocket)
    false))
