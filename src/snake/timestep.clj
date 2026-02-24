(ns snake.timestep
  (:require
   [snake.configuration :refer [base-interval min-interval speed-step speed-increase]]
   [snake.game-logic :refer [step-snake]]
   [quil.core :as q]))

;; ============================================
;; Fixed Timestep Update
;; ============================================

(defn current-interval [score]
  (let [reduction (* (quot score speed-step)
                     speed-increase)
        interval (- base-interval reduction)]
    (max min-interval interval)))

(defn update-state [state]
  (cond
    (:game-over? state) state
    (:paused? state) state
    :else
    (let [now (q/millis)]
      (if (> (- now (:last-move-time state))
             (current-interval (:score state)))
        (-> state
            step-snake
            (assoc :last-move-time now))
        state))))

