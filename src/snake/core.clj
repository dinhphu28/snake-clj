(ns snake.core
  (:require
   [snake.configuration :refer [cell-size board-width board-height]]
   [snake.game-logic :refer [initial-state]]
   [snake.timestep :refer [update-state]]
   [snake.rendering :refer [draw-state]]
   [snake.input-handling :refer [key-pressed]]
   [quil.core :as q]
   [quil.middleware :as m])
  (:gen-class))

(defn -main [& _]
  (q/defsketch snake
    :title "Snake (State Machine)"
    :size [(* board-width cell-size)
           (* board-height cell-size)]
    :setup (fn []
             (q/frame-rate 60) ;; smooth rendering
             (initial-state))
    :update update-state
    :draw draw-state
    :key-pressed key-pressed
    :middleware [m/fun-mode]))
