(ns aoc.util
  (:refer-clojure :exclude [group-by])
  (:require [clojure.core :as cc]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(defn abs [n]
  (if (neg? n)
    (- 0 n)
    n))

(defn neg [n]
  (- 0 n))

(defn manhattan-dist
  ([x y]
   (+ (abs x) (abs y)))
  ([x y z]
   (+ (abs x) (abs y) (abs z))))

(defn parse-number [s]
  (edn/read-string s))

; Reads only the first expr.
#_ (parse-number "\n 18 15 \n")

; Reads ANY edn expression, not just numbers.
#_ (parse-number "\n \"18\" 15 \n")

(defn parse-binary [s]
  (edn/read-string (str "2r" (str/triml s))))

(defn parse-octal [s]
  (edn/read-string (str "0" (str/triml s))))

(defn parse-hexa [s]
  (edn/read-string (str "0x" (str/triml s))))

#_ (str/triml " \n caFE \n ")
#_ (parse-hexa " \n caFE \n ")

(defn parse-base-n [n s]
  (edn/read-string (str n "r" (str/triml s))))

#_ (parse-base-n 36 "10") ; [0-9a-zA-Z]*


(defmacro first-for [& for-args]
  `(first (for ~@for-args)))

(defmacro forv [& for-args]
  `(vec (for ~@for-args)))


(defmacro comp-> [& args]
  `(comp ~@(reverse args)))

#_ ((comp str inc) 17) ; => "18"
#_ ((comp-> inc str) 17) ; => "18"


(defn group-by
  "Same as clojure.core/group-by, but with some handy new arities which apply
   custom map & reduce operations to the elements grouped together under the same key."
  ([kf coll]
   ;(group-by kf identity conj [] coll)
   (cc/group-by kf coll))
  ([kf vf coll]
   (group-by kf vf conj [] coll))
  ([kf vf rf coll]
   (group-by kf vf rf (rf) coll))
  ([kf vf rf init coll]
   (->> coll
        (reduce (fn [ret x]
                  (let [k (kf x)
                        v (vf x)]
                    (assoc! ret k (rf (get ret k init) v))))
                (transient {}))
        persistent!)))

#_ (group-by first [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]])
#_ (group-by first second [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]])
#_ (group-by first second + [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]])
#_ (group-by first second + 10 [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]])


(defn partitionv
  ([n vec-coll]
   (partitionv n n vec-coll))
  ([n step vec-coll]
   (for [i (range n (inc (count vec-coll)) step)]
     (subvec vec-coll (- i n) i))))

#_ (partitionv 3 (vec (range 10)))
#_ (partitionv 3 1 (vec (range 10)))


(defn partitionv-all
  [n vec-coll]
  (let [coll-size (count vec-coll)]
    (for [i (range 0 coll-size n)]
      (subvec vec-coll i (min (+ i n) coll-size)))))

#_ (partitionv-all 3 (vec (range 10)))


(defn partition-indexes
  "Returns pairs of indexes [min sup] to be used with subvec,
   where min is included and sup is excluded."
  ([n coll-size]
   (partition-indexes n n coll-size))
  ([n step coll-size]
   (for [i (range n (inc coll-size) step)]
     [(- i n) i])))

#_ (partition-indexes 3 10)
#_ (partition-indexes 3 1 10)


(defn partition-all-indexes
  "Returns pairs of indexes [min sup] to be used with subvec,
   where min is included and sup is excluded."
  [n coll-size]
  (for [i (range 0 coll-size n)]
    [i (min (+ i n) coll-size)]))

#_ (partition-all-indexes 3 10)


(defn reverse-range
  ([end] (range (dec end) -1 -1))
  ([start end] (range (dec end) (dec start) -1))
  ([start end step]
   (range (-> (- end step)
              (+ (mod (- start end) step)))
          (- start step)
          (- 0 step))))

#_ (reverse-range 10)
#_ (reverse-range 5 10)
#_ (reverse-range 5 10 3)
#_ (reverse-range 10 5 -3)

#_ (for [start (range -15 15)
         end (range -15 15)
         :when (not= (reverse-range start)
                     (reverse (range start)))]
     start)

#_ (for [start (range -15 15)
         end (range -15 15)
         :when (not= (reverse-range start end)
                     (reverse (range start end)))]
     [start end])

#_ (for [start (range -15 15)
         end (range -15 15)
         step (range -15 15)
         :when (not (zero? step))
         :when (not= (reverse-range start end step)
                     (reverse (range start end step)))]
     [start end step])
