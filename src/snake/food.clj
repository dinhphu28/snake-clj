(ns snake.food
  (:require
   [quil.core :as q]
   [snake.configuration :refer [board-width board-height]]))

(def food-types
  {:normal {:color [255 0 0]
            :score 1}

   :bonus {:color [255 215 0]
           :score 5
           :lifetime 3000}

   :slow {:color [0 150 255]
          :score 1
          :lifetime 3000
          :effect :slow}

   :fast {:color [255 0 255]
          :score 3
          :lifetime 3000
          :effect :fast}})

(def effect-durations 5000)

(defn random-food-position []
  [(rand-int board-width)
   (rand-int board-height)])

(defn random-special-food-type []
  (let [r (rand)]
    (cond
      (< r 0.85) :bonus
      (< r 0.95) :slow
      :else :fast)))

(defn random-empty-cell [snake]
  (loop []
    (let [pos (random-food-position)]
      (if (some #{pos} snake)
        (recur)
        pos))))

(defn spawn-food [snake type]
  (let [pos (random-empty-cell snake)]
    {:pos pos
     :type type
     :spawn-time (q/millis)}))

(defn maybe-spawn-special-food [state]
  (if (and (nil? (:special-food state))
           (< (rand) 0.01))
    (assoc state :special-food
           (spawn-food (:snake state)
                       (random-special-food-type)))

    state))

(defn update-special-food-timeout [state]
  (if-let [sf (:special-food state)]
    (let [type (:type sf)
          lifetime (get-in food-types [type :lifetime])]
      (if (and lifetime
               (> (- (q/millis) (:spawn-time sf))
                  lifetime))
        (assoc state :special-food nil)
        state))
    state))

(defn update-effect-timeout [state]
  (if-let [ef (:active-effect state)]
    (if (and ef
             (> (q/millis) (:effect-until state)))
      (-> state
          (assoc :active-effect nil
                 :effect-until 0))
      state)
    state))
