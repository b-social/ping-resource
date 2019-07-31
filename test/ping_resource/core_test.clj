(ns ping-resource.core-test
  (:require
    [clojure.test :refer :all]

    [ring.mock.request :as ring]
    [ping-resource.core :as core]

    [liberator-mixin.json.core :as json]
    [halboy.resource :as hal]
    [clojure.string :as str]))

(defn call-resource [resource request]
  (->
    (resource request)
    (update :body json/wire-json->map)
    ))

(deftest build-ping-resource-success
  (testing "with-routes-in-context"
    (testing "adds routes to the context"
      (let [routes [["/ping" :ping]]
            resource (core/build-ping-resource routes)
            response (call-resource
                       resource
                       (ring/request :get "/ping"))]
        (is (some? (:body response))))))

  (testing "with-self-link"
    (testing "adds a self link to context"
      (let [routes ["/ping" :ping]
            resource (core/build-ping-resource routes)
            response (call-resource
                       resource
                       (ring/request :get "/ping"))]
        (is (str/ends-with?
              (get-in response [:body :_links :self :href])
              "/ping")))))

  (deftest ping-resource-GET-on-success
    (let [routes ["/ping" :ping]
          resource (core/build-ping-resource routes)
          response (-> (call-resource
                         resource
                         (ring/request :get "/ping"))
                     :body
                     halboy.json/map->resource)
          ]
      (testing "includes a self link"
        (is (str/ends-with? (hal/get-href response :self) "/ping")))

      (testing "includes a link to discovery"
        (is (some? (hal/get-href response :discovery) )))

      (testing "returns a pong message"
          (is (= "pong" (hal/get-property response :message)))))))