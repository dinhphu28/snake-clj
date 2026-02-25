(ns snake.input-handling
  (:require
   [snake.game-logic :refer [initial-game valid-turn?]]
   [quil.core :as q]))

;; ============================================
;; Input Handling
;; ============================================

(def ENTER_KEY_CODE 10)

(defn key-pressed [state event]
  (let [k  (some-> (:key event))
        kc (:key-code event)]
    (case (:mode state)

      :menu
      (if (= kc ENTER_KEY_CODE)
        (initial-game)
        state)

      :playing
      (cond
        (= k :p)
        (assoc state :mode :paused)

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
                   (nil? (:next-dir state)))
            (assoc state :next-dir desired)
            state)))

      :paused
      (cond
        (= k :p)
        (assoc state :mode :playing
               :last-move-time (q/millis))
        :else state)

      :game-over
      (if (= k :r)
        (initial-game)
        state)

      state)))

