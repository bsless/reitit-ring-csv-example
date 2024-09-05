(ns bsless.ring-muuntaja-csv-example.format.csv
  (:refer-clojure :exclude [format])
  (:require
   [clojure.data.csv :as csv]
   [muuntaja.format.core :as core])
  (:import (java.io  OutputStream)))

(defn encoder [_]
  (reify
    core/EncodeToBytes
    (encode-to-bytes [_ data charset]
      (let [os (java.io.ByteArrayOutputStream.)
            w (java.io.OutputStreamWriter. os ^String charset)]
        (csv/write-csv w data)
        (.flush w)
        (.toByteArray os)))
    core/EncodeToOutputStream
    (encode-to-output-stream [_ data charset]
      (fn [^OutputStream output-stream]
        (let [w (java.io.OutputStreamWriter. output-stream ^String charset)]
          (csv/write-csv w data))))))

(def format
  (core/map->Format
   {:name "application/csv"
    :encoder [encoder]}))
