(ns ns1)

(defn recursive-sum [numbers]
      (if (empty? numbers)
        0
        (+ (first numbers) (recursive-sum (rest numbers)))
        )
      )
(defn main [args]
      (println recursive-sum [1, 2, 4])
      )
