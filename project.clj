(defproject bsless/ring-muuntaja-csv-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [org.clojure/data.csv "1.1.0"]
                 [metosin/jsonista "0.3.8"]
                 [ring/ring-jetty-adapter "1.12.1"]
                 [metosin/reitit "0.7.2"]
                 [metosin/ring-swagger-ui "5.9.0"]]
  :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]]}})
