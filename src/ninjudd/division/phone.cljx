(ns ninjudd.division.phone
  (:require [clojure.string :refer [lower-case]]
            [ninjudd.division :refer [all digit either ignore is-not letter many maybe
                                      one parse str* transform verify]]))

(def min-phone 10)

(defn phone []
  (-> (str*
       (all (maybe (one \+))
            (str*
             (many (either (digit)
                           (ignore (is-not (letter))))))))
      (verify (fn [number]
                (<= min-phone (count number))))
      (transform (fn [number]
                   (case (first number)
                     \+ number
                     \1 (str "+" number)
                     (if (= min-phone (count number))
                       (str "+1" number)
                       (str "+" number)))))))

(def parse-phone (parse phone))
