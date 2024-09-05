(ns bsless.ring-muuntaja-csv-example
  (:require [reitit.ring :as ring]
            [reitit.ring.spec]
            [reitit.coercion.malli]
            [reitit.openapi :as openapi]
            [reitit.ring.malli]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.dev :as dev]
            [ring.adapter.jetty :as jetty]
            [malli.core :as malli]
            [bsless.ring-muuntaja-csv-example.format.csv :as csv-format]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]))

(def Transaction
  [:map
   [:amount :double]
   [:from :string]])

(def AccountId
  [:map
   [:bank :string]
   [:id :string]])

(def Account
  [:map
   [:bank :string]
   [:id :string]
   [:balance :double]
   [:transactions [:vector #'Transaction]]])

(def muuntaja
  (m/create
   (-> m/default-options
       (assoc-in [:formats "text/csv"] csv-format/format)
       (assoc :return :bytes))))

(slurp (m/encode muuntaja "text/csv" [["color" "pineapple"] ["red" true]] "utf-8"))

(defn app
  []
  (ring/ring-handler
    (ring/router
      [["/csv"
        {:get {:responses {200 {:content {"text/csv" {:description "Fetch a pizza as csv"}}}}
               :handler (fn [_request]
                          {:status 200
                           :headers {"content-type" "text/csv"}
                           :body [["color" "pineapple"] ["red" true]]})}}]]

      {:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       :validate reitit.ring.spec/validate
       :exception pretty/exception
       :data {:coercion reitit.coercion.malli/coercion
              :muuntaja muuntaja
              :middleware [openapi/openapi-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
     (swagger-ui/create-swagger-ui-handler
      {:path "/"
       :config {:validatorUrl nil
                :urls [{:name "openapi", :url "openapi.json"}]
                :urls.primaryName "openapi"
                :operationsSorter "alpha"}})
     (ring/create-default-handler))))

(-> (mock/request :get "/csv")
    (mock/header "accept" "text/csv")
    ((app))
    :body
    slurp
    )


(defn start []
  (jetty/run-jetty #'app {:port 3000, :join? false})
  (println "server running in port 3000"))

(comment
  (start))
