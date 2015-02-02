(ns ninjudd.division.phone-test
  (:use clojure.test ninjudd.division.test-helper)
  (:require [ninjudd.division.phone :refer [phone]]))

(deftest test-phone
  (let [parse-phone (remove-consumed (phone))]
    (are [identifier string]
         (= (when identifier [identifier ()])
            (parse-phone string))
         "+15058221801"  "505-822-1801"
         "+15058221801"  "+1-505-822-1801"
         "+213105551234" "+21-3105551234"
         "+15058221801"  "(505)-822-1801"
         "+15058221801"  "+ 1 505 822 1801"
         nil             "num:5058221801"
         nil             "1234"
         nil             "822-1801")))
