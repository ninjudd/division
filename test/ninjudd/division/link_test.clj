(ns ninjudd.division.link-test
  (:use clojure.test ninjudd.division.test-helper)
  (:require [ninjudd.division.link :refer [link-with-scheme inferred-link scan-links]]))

(deftest test-link-with-scheme
  (let [parse-link (remove-consumed (link-with-scheme))]
    (are [result string]
         (= (when result [result ()])
            (parse-link string))
         {:scheme "https", :host "swim.is", :port 3000, :path "foo/bar",
          :query "query=string&a=b", :fragment "frament"}
         "https://swim.is:3000/foo/bar?query=string&a=b#frament"
         {:scheme "http", :host "google.com", :query "query=string"}
         "http://google.com?query=string"
         {:scheme "http", :host "localhost", :port 5124}
         "http://localhost:5124"
         {:scheme "ftp", :host "ftp.com", :path "flowers", :fragment "yellow"}
         "ftp://ftp.com/flowers#yellow"
         {:scheme "swim", :host "123456"}
         "swim://123456"
         nil "http://"
         nil "foo.bar")))

(deftest test-inferred-link
  (let [parse-link (remove-consumed (inferred-link))]
    (are [result string]
         (= (when result [result ()])
            (parse-link string))
         {:host "swim.is"} "swim.is"
         {:host "github.io" :path "ninjudd"} "github.io/ninjudd"
         nil "a.b/c"
         nil "com/index.html"
         nil "foo.bar.baz")))

(deftest test-scan-links
  (are [result string] (= result (scan-links string))
       ["Hey Noah, check out "
        {:host "swim.is" :url "http://swim.is"}
        "."]
       "Hey Noah, check out swim.is."
       ["Follow me at "
        {:host "twitter.com" :path "ninjudd" :scheme "http" :url "http://twitter.com/ninjudd"}
        "!!!?..."]
       "Follow me at http://twitter.com/ninjudd!!!?..."
       ["My favorites ("
        {:url "http://a.co", :host "a.co"} ", "
        {:url "http://b.io", :host "b.io"} "; "
        {:url "http://c.sh", :host "c.sh"} ")!!!"]
       "My favorites (a.co, b.io; c.sh)!!!"))
