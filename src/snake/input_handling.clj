(ns snake.input-handling
  (:require
   [snake.game-logic :refer [initial-state valid-turn?]]))

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
