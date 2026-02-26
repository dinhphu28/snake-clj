(ns snake.configuration)

;; ============================================
;; Configuration
;; ============================================

(def cell-size 20)
(def board-width 30)
(def board-height 20)

(def base-interval 200)
(def min-interval 10)
(def speed-step 5) ;; every 5 points
(def speed-increase 10) ;; reduce 10ms each step

(def directions
  {:up    [0 -1]
   :down  [0 1]
   :left  [-1 0]
   :right [1 0]})

(def opposite
  {:up :down
   :down :up
   :left :right
   :right :left})

