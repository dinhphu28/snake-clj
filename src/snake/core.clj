(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.string :as str])
  (:gen-class))

;; ============================================
;; Configuration
;; ============================================

(def cell-size 20)
(def board-width 30)
(def board-height 20)

;; (def move-interval 500) ;; milliseconds per snake step

(def base-interval 120)
(def min-interval 50)
(def speed-step 5) ;; every 5 points
(def speed-increase 10) ;; reduce 10ms each step

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

;; ============================================
;; Game Logic (Pure)
;; ============================================

(defn random-food []
  [(rand-int board-width)
   (rand-int board-height)])

(defn initial-state []
  {:snake [[15 10]]
   :dir :right
   :next-dir nil
   :food (random-food)
   :score 0
   :game-over? false
   :last-move-time 0})

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
      (assoc state :game-over? true)

      (collision? new-head snake)
      (assoc state :game-over? true)

      ate?
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head snake)
                 :food (random-food))
          (update :score inc))

      :else
      (-> state
          (assoc :dir dir-key
                 :next-dir nil
                 :snake (cons new-head (butlast snake)))))))

;; ============================================
;; Fixed Timestep Update
;; ============================================

(defn current-interval [score]
  (let [reduction (* (quot score speed-step)
                     speed-increase)
        interval (- base-interval reduction)]
    (max min-interval interval)))

(defn update-state [state]
  (if (:game-over? state)
    state
    (let [now (q/millis)]
      ;; (if (> (- now (:last-move-time state)) move-interval)
      (if (> (- now (:last-move-time state))
             (current-interval (:score state)))
        (-> state
            step-snake
            (assoc :last-move-time now))
        state))))

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

;; ============================================
;; Input Handling
;; ============================================

(defn key-pressed [state event]
  (let [k  (some-> (:key event))
        kc (:key-code event)]
    (cond
      ;; Restart
      (= k :r)
      (initial-state)

      ;; Direction input
      :else
      (let [desired
            (cond
              (= k :up) :up
              (= k :down) :down
              (= k :left) :left
              (= k :right) :right
              (= k :w) :up
              (= k :s) :down
              (= k :a) :left
              (= k :d) :right
              :else nil)]
        (if (and desired
                 (valid-turn? (:dir state) desired)
                 (nil? (:next-dir state))) ;; only one turn per tick
          (assoc state :next-dir desired)
          state)))))

;; ============================================
;; Entry Point
;; ============================================

(defn -main [& _]
  (q/defsketch snake
    :title "Snake (Fixed Timestep)"
    :size [(* board-width cell-size)
           (* board-height cell-size)]
    :setup (fn []
             (q/frame-rate 60) ;; smooth rendering
             (initial-state))
    :update update-state
    :draw draw-state
    :key-pressed key-pressed
    :middleware [m/fun-mode]))
