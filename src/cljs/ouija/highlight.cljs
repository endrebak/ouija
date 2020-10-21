(ns ouija.highlight
  (:require [com.rpl.specter :refer [transform]]))


(defn highlight-select [q m]
  (transform q #(tagged-literal 'highlight %) m))


(defn highlight-transform [q f m]
  (transform q #(tagged-literal 'highlight (f %)) m)) 


(defn highlight
  ([q m] (highlight-select [q m]))
  ([q f m] (highlight-transform [q f m]))) 


(defn result
  ([q m] (select q m))
  ([q f m] (transform q f m)))



(comment
  (use 'com.rpl.specter)
  (+ 1)   
  (def q [MAP-VALS MAP-VALS])  
  (def m {:a {:aa 1} :b {:ba -1 :bb 2}}) 
  (highlight q #(+ % 42) m) 
  (highlight-select q m)  
  ; (highlight-transform q inc m)
  ) 
