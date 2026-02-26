(ns snake.timestep
  (:require
   [snake.configuration :refer [base-interval min-interval speed-step speed-increase]]
   [snake.game-logic :refer [step-snake]]
   [snake.food :refer [maybe-spawn-special-food update-special-food-timeout update-effect-timeout]]
   [quil.core :as q]))

;; ============================================
;; Fixed Timestep Update
;; ============================================

(defn effect-interval-reduction [state curr-interval]
  (if-let [ef (:active-effect state)]
    (cond
      (= ef :slow) (quot curr-interval -2)
      (= ef :fast) (quot curr-interval 2)
      :else 0)
    0))

(defn current-interval [state]
  (let [score (:score state)
        reduction (* (quot score speed-step)
                     speed-increase)
        interval (- base-interval reduction)
        effect-reduction (effect-interval-reduction state interval)
        final-interval (- interval effect-reduction)]

    (max min-interval final-interval)))

(defn update-state [state]
  (case (:mode state)

    :playing
    (let [now (q/millis)]
      (if (> (- now (:last-move-time state))
             (current-interval state))

        (-> state
            maybe-spawn-special-food
            step-snake
            update-special-food-timeout
            update-effect-timeout
            (assoc :last-move-time now))

        state))

    ;; other modes do not update game logic
    state))

