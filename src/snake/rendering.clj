(ns snake.rendering
  (:require
   [snake.configuration :refer [cell-size board-width board-height]]
   [snake.food :refer [food-types]]
   [quil.core :as q]))

;; ============================================
;; Rendering
;; ============================================

(defn draw-cell [[x y] [r g b]]
  (q/fill r g b)
  (q/rect (* x cell-size)
          (* y cell-size)
          cell-size
          cell-size))

(defn draw-food [food]
  (case (:type food)

    :normal
    (draw-cell (:pos food)
               (get-in food-types [:normal :color]))

    :bonus
    (draw-cell (:pos food)
               (get-in food-types [:bonus :color]))

    :slow
    (draw-cell (:pos food)
               (get-in food-types [:slow :color]))

    :fast
    (draw-cell (:pos food)
               (get-in food-types [:fast :color]))))

(defn draw-playing [state]
  (draw-food (:food state))

  (when-let [special-food (:special-food state)]
    (draw-food special-food))

  (doseq [segment (:snake state)]
    (draw-cell segment [0 200 0]))

  (q/push-style)
  (q/fill 255)
  (q/text-align :left :top)
  (q/text-size 16)
  (q/text (str "Score: " (:score state)) 10 10)
  (q/pop-style))

(defn draw-centered-text [text size y-offset]
  (q/fill 255)
  (q/text-align :center :center)
  (q/text-size size)
  (q/text text
          (/ (* board-width cell-size) 2)
          (+ (/ (* board-height cell-size) 2)
             y-offset)))

(defn draw-state [state]
  (q/background 30)
  (case (:mode state)

    :menu
    (do
      (draw-centered-text "SNAKE" 48 -40)
      (draw-centered-text "Press ENTER to Start" 18 30))

    :playing
    (draw-playing state)

    :paused
    (do
      (draw-playing state)
      (draw-centered-text "PAUSED" 32 0))
    :game-over
    (do
      (draw-playing state)
      (draw-centered-text "GAME OVER" 32 0)
      (draw-centered-text "Press R to Restart" 18 30))))

