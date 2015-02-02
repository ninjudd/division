(defproject com.ninjudd/division "0.1.0-SNAPSHOT"
  :description "Simple parser combinator for Clojure and Clojurescript."
  :url "http://github.com/ninjudd/division"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.5.0"]]}}
  :cljx {:builds [{:rules :clj
                   :source-paths ["src"]
                   :output-path "target/classes"}
                  {:rules :cljs
                   :source-paths ["src"]
                   :output-path "target/classes"}]}
  :prep-tasks [["cljx" "once"]])

