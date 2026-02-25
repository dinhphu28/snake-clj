(ns snake.game-logic
  (:require
   [snake.configuration :refer [board-width board-height directions opposite]]))

;; ============================================
;; Game Logic (Pure)
;; ============================================

(defn random-food-position []
  [(rand-int board-width)
   (rand-int board-height)])

(defn spawn-food [snake]
  (loop []
    (let [pos (random-food-position)]
      (if (some #{pos} snake)
        (recur)
        pos))))

(defn initial-game []
  (let [snake [[15 10]]]
    {:mode :playing
     :snake snake
     :dir :right
     :next-dir nil
     :food (spawn-food snake)
     :score 0
     :last-move-time 0}))

(defn initial-state []
  {:mode :menu})

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
      (assoc state :mode :game-over)

      (collision? new-head snake)
      (assoc state :mode :game-over)

      ate?
      (let [new-snake (cons new-head snake)]
        (-> state
            (assoc :dir dir-key
                   :next-dir nil
                   :snake new-snake
                   :food (spawn-food new-snake))
            (update :score inc)))

      :else
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head (butlast snake)))))))

