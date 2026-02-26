(ns snake.game-logic
  (:require
   [quil.core :as q]
   [snake.configuration :refer [board-width board-height directions opposite]]
   [snake.food :refer [spawn-food food-types effect-durations]]))

;; ============================================
;; Game Logic (Pure)
;; ============================================

(defn initial-game []
  (let [snake [[15 10]]]
    {:mode :playing
     :snake snake
     :dir :right
     :next-dir nil
     :score 0
     :food (spawn-food snake :normal)
     :special-food nil
     :active-effect nil
     :effect-until 0
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
        ate?    (= new-head (:pos (:food state)))
        ate-special? (and (:special-food state)
                          (= new-head (:pos (:special-food state))))]
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
                   :food (spawn-food new-snake :normal))
            (update :score inc)))

      ate-special?
      (let [new-snake (cons new-head snake)
            sf (:special-food state)
            ef (get-in food-types [(:type sf) :effect])]
        (-> state
            (assoc :dir dir-key
                   :next-dir nil
                   :snake new-snake
                   :special-food nil
                   :active-effect ef
                   :effect-until (+ (q/millis) effect-durations))
            (update :score + (get-in food-types [(:type (:special-food state)) :score]))))

      :else
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head (butlast snake)))))))

