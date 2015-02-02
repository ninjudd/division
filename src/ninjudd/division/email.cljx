(ns ninjudd.division.email
  (:require [clojure.string :refer [lower-case]]
            [ninjudd.division :refer [all either many one prefix str* symbol-char
                                      token transform word-char]]))

(defn email []
  (-> (str*
       (all (str*
             (-> (either (symbol-char)
                         (token #{\+ \.}))
                 (many 1)))
            (one \@)
            (str* (many (symbol-char) 1))
            (str* (-> (str*
                       (prefix (one \.)
                               (many (word-char) 1)))
                      (many 1)))))
      (transform lower-case)))
