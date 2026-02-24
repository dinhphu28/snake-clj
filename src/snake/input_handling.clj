(ns snake.input-handling
  (:require
   [snake.game-logic :refer [initial-state valid-turn?]]
   [quil.core :as q]))

;; ============================================
;; Input Handling
;; ============================================

(defn key-pressed [state event]
  (let [k  (some-> (:key event))]
    (cond
      ;; Restart
      (= k :r)
      (initial-state)

      ;; Pause toggle
      (= k :p)
      (-> state
          (update :paused? not)
          ;; prevent time jump after unpause
          (assoc :last-move-time (q/millis)))

      ;; Direction input
      (:paused? state)
      state

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
