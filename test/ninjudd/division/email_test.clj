(ns ninjudd.division.email-test
  (:use clojure.test ninjudd.division.test-helper)
  (:require [ninjudd.division.email :refer [email]]))

(deftest test-email
  (let [parse-email (remove-consumed (email))]
    (are [identifier string]
         (= (when identifier [identifier ()])
            (parse-email string))
         "justin@balth.rop"      "justin@balth.rop"
         "justin+spam@balth.rop" "justin+spam@balth.rop"
         "justin@adaptive.cs.unm.edu" "justin@adaptive.cs.unm.edu"
         "12345@google.com"         "12345@google.com"
         "1@2.3.4.5"                "1@2.3.4.5"
         "_not-me.@a.com"           "_Not-me.@A.com"
         nil                        "555-3428"
         nil                        "@foo.com"
         nil                        "justin@balthrop")))
