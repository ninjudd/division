(ns ninjudd.division.test-helper)

(defn remove-consumed [parse]
  (fn [stream]
    (when-let [[val consumed stream] (parse stream)]
      [val stream])))
