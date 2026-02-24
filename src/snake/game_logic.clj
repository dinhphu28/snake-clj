(ns snake.game-logic
  (:require
   [snake.configuration :refer [board-width board-height directions opposite]]))

;; ============================================
;; Game Logic (Pure)
;; ============================================

(defn random-food []
  [(rand-int board-width)
   (rand-int board-height)])

(defn initial-state []
  {:snake [[15 10]]
   :dir :right
   :next-dir nil
   :food (random-food)
   :score 0
   :game-over? false
   :last-move-time 0})

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn wall-hit? [[x y]]
  (or (< x 0)
      (>= x board-width)
      (< y 0)
      (>= y board-height)))

(defn collision? [pos snake]
  (some #{pos} snake))

(defn valid-turn? [current next]
  (not= current (opposite next)))

(defn step-snake [state]
  (let [dir-key (or (:next-dir state) (:dir state))
        dir     (directions dir-key)
        head    (first (:snake state))
        new-head (move head dir)
        snake   (:snake state)
        ate?    (= new-head (:food state))]
    (cond
      (wall-hit? new-head)
      (assoc state :game-over? true)

      (collision? new-head snake)
      (assoc state :game-over? true)

      ate?
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head snake)
                 :food (random-food))
          (update :score inc))

      :else
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head (butlast snake)))))))

