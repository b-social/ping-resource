(ns ping-resource.core
  (:require
    [liberator-mixin.hypermedia.core :as urls]
    [liberator-mixin.core :refer [build-resource]]
    [liberator-mixin.json.core :refer [with-json-mixin]]
    [liberator-mixin.validation.core :refer [with-validation-mixin]]
    [liberator-mixin.hypermedia.core :refer [with-hypermedia-mixin]]
    [liberator-mixin.hal.core :refer [with-hal-mixin]]
    [liberator.util :refer [make-function]]
    [halboy.resource :as hal]
    [bidi.bidi :refer [path-for]]))

(defn ping-response []
  {:message "pong"})

(defn build-ping-resource [routes]
  (build-resource
    (with-json-mixin nil)
    (with-hypermedia-mixin nil)
    (with-hal-mixin nil)
    {:handle-ok
     (fn [{:keys [request]}]
       (hal/add-properties
         (hal/new-resource
           {:href (urls/absolute-url-for request routes :ping)})
         (ping-response)))}))
