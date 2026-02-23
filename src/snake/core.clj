(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))

;; =============================
;; Configuration
;; =============================

(def cell-size 20)
(def board-width 30)
(def board-height 20)

(def directions
  {:up    [0 -1]
   :down  [0 1]
   :left  [-1 0]
   :right [1 0]})

;; =============================
;; Game Logic (Pure)
;; =============================

(defn random-food []
  [(rand-int board-width)
   (rand-int board-height)])

(defn initial-state []
  {:snake [[15 10]]
   :dir :right
   :next-dir :right
   :food (random-food)
   :score 0
   :game-over? false})

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn collision? [pos snake]
  (some #{pos} snake))

(defn wall-hit? [[x y]]
  (or (< x 0)
      (>= x board-width)
      (< y 0)
      (>= y board-height)))

(defn step [state]
  (if (:game-over? state)
    state
    (let [dir       (directions (:dir state))
          head      (first (:snake state))
          new-head  (move head dir)
          snake     (:snake state)
          ate-food? (= new-head (:food state))]
      (cond
        (wall-hit? new-head)
        (assoc state :game-over? true)

        (collision? new-head snake)
        (assoc state :game-over? true)

        ate-food?
        (-> state
            (update :snake #(cons new-head %))
            (assoc :food (random-food))
            (update :score inc))

        :else
        (-> state
            (update :snake #(cons new-head (butlast %))))))))

;; =============================
;; Rendering
;; =============================

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

  ;; Game Over
  (when (:game-over? state)
    (q/text-size 32)
    (q/text "GAME OVER"
            (/ (* board-width cell-size) 4)
            (/ (* board-height cell-size) 2))
    (q/text-size 16)
    (q/text "Press R to restart"
            (/ (* board-width cell-size) 3)
            (+ (/ (* board-height cell-size) 2) 30))))

;; =============================
;; Input
;; =============================

(def opposite
  {:up :down
   :down :up
   :left :right
   :right :left})

;; =============================
;; Quil Sketch
;; =============================

;; (defn key-pressed [state event]
;;   (println event)
;;   state)

(defn key-pressed [state event]
  (let [k  (:key event)]
    (cond
      (= k :up) (if (= (:dir state) :down) state (assoc state :dir :up))
      (= k :down) (if (= (:dir state) :up) state (assoc state :dir :down))
      (= k :left) (if (= (:dir state) :right) state (assoc state :dir :left))
      (= k :right) (if (= (:dir state) :left) state (assoc state :dir :right))

      ;; WASD
      (= k :w) (if (= (:dir state) :down) state (assoc state :dir :up))
      (= k :s) (if (= (:dir state) :up) state (assoc state :dir :down))
      (= k :a) (if (= (:dir state) :right) state (assoc state :dir :left))
      (= k :d) (if (= (:dir state) :left) state (assoc state :dir :right))

      ;; Restart
      (= k :r) (initial-state)

      :else state)))

(defn -main [& _]
  (q/defsketch snake
    :title "Snake (Clojure + Quil)"
    :size [(* board-width cell-size)
           (* board-height cell-size)]
    :setup (fn []
             (q/frame-rate 5)
             (initial-state))
    :update step
    :draw draw-state
    :key-pressed key-pressed
    :middleware [m/fun-mode]))
