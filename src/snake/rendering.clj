(ns snake.rendering
  (:require
   [snake.configuration :refer [cell-size board-width board-height]]
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

(defn draw-state [state]
  (q/background 30)

  ;; Food
  (draw-cell (:food state) [255 0 0])

  ;; Snake
  (doseq [segment (:snake state)]
    (draw-cell segment [0 200 0]))

  ;; Score
  (q/fill 255)
  (q/text-size 16)
  (q/text (str "Score: " (:score state)) 10 20)

  (when (:game-over? state)
    (q/text-size 32)
    (q/text "GAME OVER"
            (/ (* board-width cell-size) 4)
            (/ (* board-height cell-size) 2))
    (q/text-size 16)
    (q/text "Press R to restart"
            (/ (* board-width cell-size) 3)
            (+ (/ (* board-height cell-size) 2) 30))))

