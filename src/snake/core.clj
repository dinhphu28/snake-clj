(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.string :as str])
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

(def opposite
  {:up :down
   :down :up
   :left :right
   :right :left})

(def KEY-UP 38)
(def KEY-DOWN 40)
(def KEY-LEFT 37)
(def KEY-RIGHT 39)

;; =============================
;; Pure Game Logic
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

(defn wall-hit? [[x y]]
  (or (< x 0)
      (>= x board-width)
      (< y 0)
      (>= y board-height)))

(defn collision? [pos snake]
  (some #{pos} snake))

(defn valid-turn? [current next]
  (not= current (opposite next)))

(defn step [state]
  (if (:game-over? state)
    state
    (let [;; Apply buffered direction at start of tick
          state     (assoc state :dir (:next-dir state))
          dir       (directions (:dir state))
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

  ;; Game Over Overlay
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
;; Input Handling
;; =============================

(defn key-pressed [state event]
  ;; (let [k  (some-> (:key event) str str/lower-case)
  (let [k  (some-> (:key event))
        kc (:key-code event)]

    ;; Restart ALWAYS works
    (when (= k :r)
      (println "Restart pressed"))

    (if (= k :r)
      (initial-state)

      (let [desired
            (cond
              (= kc KEY-UP) :up
              (= kc KEY-DOWN) :down
              (= kc KEY-LEFT) :left
              (= kc KEY-RIGHT) :right
              (= k :w) :up
              (= k :s) :down
              (= k :a) :left
              (= k :d) :right
              :else nil)]

        (if (and desired
                 (valid-turn? (:dir state) desired))
          (assoc state :next-dir desired)
          state)))))

;; =============================
;; Quil Sketch Entry Point
;; =============================

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
