(ns muharni.construct
  "Construct 'Muharni' tables used in learning Gurmukhi script, a script
   devised and tradiitionally used for Sikh religious texts."
  (:require [hiccup.core :refer [html]]
            [clojure.java.io :refer [input-stream]]
            [clojure.string :as s])
  (:import [java.io StringWriter PrintWriter]
           [java.util Properties]
           [org.w3c.tidy Tidy]))


(def entries
  "Entries in the table. Note that I'm not clear whether these entries
   represent individual characters, or syllables, but for my purposes
   it doesn't matter"
  [["ਅ" "ਆ" "ਇ" "ਈ" "ਉ" "ਊ" "ਏ" "ਐ" "ਓ" "ਔ" "ਅੰ" "ਆਂ"]
   ["ਸ" "ਸਾ" "ਸਿ" "ਸੀ" "ਸੁ" "ਸੂ" "ਸੇ" "ਸੈ" "ਸੋ" "ਸੌ" "ਸੰ" "ਸਾਂ"]
   ["ਹ" "ਹਾ" "ਹਿ" "ਹੀ" "ਹੁ" "ਹੂ" "ਹੇ" "ਹੈ" "ਹੋ" "ਹੌ" "ਹੰ" "ਹਾਂ"]
   ["ਕ" "ਕਾ" "ਕਿ" "ਕੀ" "ਕੁ" "ਕੂ" "ਕੇ" "ਕੈ" "ਕੋ" "ਕੌ" "ਕੰ" "ਕਾਂ"]
   ["ਖ" "ਖਾ" "ਖਿ" "ਖੀ" "ਖੁ" "ਖੂ" "ਖੇ" "ਖੈ" "ਖੋ" "ਖੌ" "ਖੰ" "ਖਾਂ"]
   ["ਗ" "ਗਾ" "ਗਿ" "ਗੀ" "ਗੁ" "ਗੂ" "ਗੇ" "ਗੈ" "ਗੋ" "ਗੌ" "ਗੰ" "ਗਾਂ"]
   ["ਘ" "ਘਾ" "ਘਿ" "ਘੀ" "ਘੁ" "ਘੂ" "ਘੇ" "ਘੈ" "ਘੋ" "ਘੌ" "ਘੰ" "ਘਾਂ"]
   ["ਙ" "ਙਾ" "ਙਿ" "ਙੀ" "ਙੁ" "ਙੂ" "ਙੇ" "ਙੈ" "ਙੋ" "ਙੌ" "ਙੰ" "ਙਾਂ"]
   ["ਚ" "ਚਾ" "ਚਿ" "ਚੀ" "ਚੁ" "ਚੂ" "ਚੇ" "ਚੈ" "ਚੋ" "ਚੌ" "ਚੰ" "ਚਾਂ"]
   ["ਛ" "ਛਾ" "ਛਿ" "ਛੀ" "ਛੁ" "ਛੂ" "ਛੇ" "ਛੈ" "ਛੋ" "ਛੌ" "ਛੰ" "ਛਾਂ"]
   ["ਜ" "ਜਾ" "ਜਿ" "ਜੀ" "ਜੁ" "ਜੂ" "ਜੇ" "ਜੈ" "ਜੋ" "ਜੌ" "ਜੰ" "ਜਾਂ"]
   ["ਝ" "ਝਾ" "ਝਿ" "ਝੀ" "ਝੁ" "ਝੂ" "ਝੇ" "ਝੈ" "ਝੋ" "ਝੌ" "ਝੰ" "ਝਾਂ"]
   ["ਞ" "ਞਾ" "ਞਿ" "ਞੀ" "ਞੁ" "ਞੂ" "ਞੇ" "ਞੈ" "ਞੋ" "ਞੌ" "ਞੰ" "ਞਾਂ"]
   ["ਟ" "ਟਾ" "ਟਿ" "ਟੀ" "ਟੁ" "ਟੂ" "ਟੇ" "ਟੈ" "ਟੋ" "ਟੌ" "ਟੰ" "ਟਾਂ"]
   ["ਠ" "ਠਾ" "ਠਿ" "ਠੀ" "ਠੁ" "ਠੂ" "ਠੇ" "ਠੈ" "ਠੋ" "ਠੌ" "ਠੰ" "ਠਾਂ"]
   ["ਡ" "ਡਾ" "ਡਿ" "ਡੀ" "ਡੁ" "ਡੂ" "ਡੇ" "ਡੈ" "ਡੋ" "ਡੌ" "ਡੰ" "ਡਾਂ"]
   ["ਢ" "ਢਾ" "ਢਿ" "ਢੀ" "ਢੁ" "ਢੂ" "ਢੇ" "ਢੈ" "ਢੋ" "ਢੌ" "ਢੰ" "ਢਾਂ"]
   ["ਣ" "ਣਾ" "ਣਿ" "ਣੀ" "ਣੁ" "ਣੂ" "ਣੇ" "ਣੈ" "ਣੋ" "ਣੌ" "ਣੰ" "ਣਾਂ"]
   ["ਤ" "ਤਾ" "ਤਿ" "ਤੀ" "ਤੁ" "ਤੂ" "ਤੇ" "ਤੈ" "ਤੋ" "ਤੌ" "ਤੰ" "ਤਾਂ"]
   ["ਥ" "ਥਾ" "ਥਿ" "ਥੀ" "ਥੁ" "ਥੂ" "ਥੇ" "ਥੈ" "ਥੋ" "ਥੌ" "ਥੰ" "ਥਾਂ"]
   ["ਦ" "ਦਾ" "ਦਿ" "ਦੀ" "ਦੁ" "ਦੂ" "ਦੇ" "ਦੈ" "ਦੋ" "ਦੌ" "ਦੰ" "ਦਾਂ"]
   ["ਧ" "ਧਾ" "ਧਿ" "ਧੀ" "ਧੁ" "ਧੂ" "ਧੇ" "ਧੈ" "ਧੋ" "ਧੌ" "ਧੰ" "ਧਾਂ"]
   ["ਨ" "ਨਾ" "ਨਿ" "ਨੀ" "ਨੁ" "ਨੂ" "ਨੇ" "ਨੈ" "ਨੋ" "ਨੌ" "ਨੰ" "ਨਾਂ"]
   ["ਪ" "ਪਾ" "ਪਿ" "ਪੀ" "ਪੁ" "ਪੂ" "ਪੇ" "ਪੈ" "ਪੋ" "ਪੌ" "ਪੰ" "ਪਾਂ"]
   ["ਫ" "ਫਾ" "ਫਿ" "ਫੀ" "ਫੁ" "ਫੂ" "ਫੇ" "ਫੈ" "ਫੋ" "ਫੌ" "ਫੰ" "ਫਾਂ"]
   ["ਬ" "ਬਾ" "ਬਿ" "ਬੀ" "ਬੁ" "ਬੂ" "ਬੇ" "ਬੈ" "ਬੋ" "ਬੌ" "ਬੰ" "ਬਾਂ"]
   ["ਭ" "ਭਾ" "ਭਿ" "ਭੀ" "ਭੁ" "ਭੂ" "ਭੇ" "ਭੈ" "ਭੋ" "ਭੌ" "ਭੰ" "ਭਾਂ"]
   ["ਮ" "ਮਾ" "ਮਿ" "ਮੀ" "ਮੁ" "ਮੂ" "ਮੇ" "ਮੈ" "ਮੋ" "ਮੌ" "ਮੰ" "ਮਾਂ"]
   ["ਯ" "ਯਾ" "ਯਿ" "ਯੀ" "ਯੁ" "ਯੂ" "ਯੇ" "ਯੈ" "ਯੋ" "ਯੌ" "ਯੰ" "ਯਾਂ"]
   ["ਰ" "ਰਾ" "ਰਿ" "ਰੀ" "ਰੁ" "ਰੂ" "ਰੇ" "ਰੈ" "ਰੋ" "ਰੌ" "ਰੰ" "ਰਾਂ"]
   ["ਲ" "ਲਾ" "ਲਿ" "ਲੀ" "ਲੁ" "ਲੂ" "ਲੇ" "ਲੈ" "ਲੋ" "ਲੌ" "ਲੰ" "ਲਾਂ"]
   ["ਵ" "ਵਾ" "ਵਿ" "ਵੀ" "ਵੁ" "ਵੂ" "ਵੇ" "ਵੈ" "ਵੋ" "ਵੌ" "ਵੰ" "ਵਾਂ"]
   ["ੜ" "ੜਾ" "ੜਿ" "ੜੀ" "ੜੁ" "ੜੂ" "ੜੇ" "ੜੈ" "ੜੋ" "ੜੌ" "ੜੰ" "ੜਾਂ"]
   ["ਸ਼" "ਸ਼ਾ" "ਸ਼ਿ" "ਸ਼ੀ" "ਸ਼ੁ" "ਸ਼ੂ" "ਸ਼ੇ" "ਸ਼ੈ" "ਸ਼ੋ" "ਸ਼ੌ" "ਸ਼ੰ" "ਸ਼ਾਂ"]
   ["ਖ਼" "ਖ਼ਾ" "ਖ਼ਿ" "ਖ਼ੀ" "ਖ਼ੁ" "ਖ਼ੂ" "ਖ਼ੇ" "ਖ਼ੈ" "ਖ਼ੋ" "ਖ਼ੌ" "ਖ਼ੰ" "ਖ਼ਾਂ"]
   ["ਗ਼" "ਗ਼ਾ" "ਗ਼ਿ" "ਗ਼ੀ" "ਗ਼ੁ" "ਗ਼ੂ" "ਗ਼ੇ" "ਗ਼ੈ" "ਗ਼ੋ" "ਗ਼ੌ" "ਗ਼ੰ" "ਗ਼ਾਂ"]
   ["ਜ਼" "ਜ਼ਾ" "ਜ਼ਿ" "ਜ਼ੀ" "ਜ਼ੁ" "ਜ਼ੂ" "ਜ਼ੇ" "ਜ਼ੈ" "ਜ਼ੋ" "ਜ਼ੌ" "ਜ਼ੰ" "ਜ਼ਾਂ"]
   ["ਫ਼" "ਫ਼ਾ" "ਫ਼ਿ" "ਫ਼ੀ" "ਫ਼ੁ" "ਫ਼ੂ" "ਫ਼ੇ" "ਫ਼ੈ" "ਫ਼ੋ" "ਫ਼ੌ" "ਫ਼ੰ" "ਫ਼ਾਂ"]
   ["ਲ਼" "ਲ਼ਾ" "ਲ਼ਿ" "ਲ਼ੀ" "ਲ਼ੁ" "ਲ਼ੂ" "ਲ਼ੇ" "ਲ਼ੈ" "ਲ਼ੋ" "ਲ਼ੌ" "ਲ਼ੰ" "ਲ਼ਾਂ"]])

;; (apply min (map count entries))

(def columns
  "This is essentially just Lucy's own encoding of the sound file names,
   I think, and has no further significance."
  [{:name "Muktā", :lower-latin "b", :upper-latin "B"}
   {:name "Kannā", :lower-latin "c", :upper-latin "C"}
   {:name "Sihārī", :lower-latin "d", :upper-latin "D"}
   {:name "Bihārī", :lower-latin "e", :upper-latin "E"}
   {:name "Auṅkaṛ", :lower-latin "f", :upper-latin "F"}
   {:name "Dulaiṅkaṛ", :lower-latin "g", :upper-latin "G"}
   {:name "Lāvā", :lower-latin "h", :upper-latin "H"}
   {:name "Dulāvā", :lower-latin "i", :upper-latin "I"}
   {:name "Hōṛā", :lower-latin "j", :upper-latin "J"}
   {:name "Kanauṛā", :lower-latin "k", :upper-latin "K"}
   {:name "Ṭippī", :lower-latin "l", :upper-latin "L"}
   {:name "Bindī", :lower-latin "m", :upper-latin "M"}])

;; (defn audio
;;   [^Integer col ^Integer row ^Boolean long?]
;;   [:audio {:controls true}
;;    [:source {:src (format "audio/%02d%s.mp3"
;;                           (inc row)
;;                           ((columns col) (if long? :upper-latin :lower-latin)))
;;              :type "audio/mpeg"}]])

(defn entry-cell
  "Emit a table cell describing one entry from entries with either the
   long or short audio clip available on click. "
  [^Integer row ^Integer col ^Boolean long?]
  (vector :td {:class "entry"

               :onclick (format
                         "new Audio('audio/%02d%s.mp3').play();"
                         (inc row)
                         ((columns col) (if long? :upper-latin :lower-latin)))}
          ;; (audio row col long?)
          ((entries row) col)))

;; (entry-cell 3 4 true)

(defn all-entries-cell
  [^Integer row ^Boolean ltr? ^Boolean long?]
  [:td {:class "play-row"
        :onclick (format "new Audio('audio/%s%s%02d_MP3WRAP.mp3').play();"
                         (if ltr? "ltr" "rtl")
                         (if long? "long" "short")
                         (inc row))}
   (str "Play " (if ltr? "LTR" "RTL"))])


(defn entries-row
  [^Integer row ^Boolean long?]
  (apply vector
         (concat [:tr]
                 [(all-entries-cell row true long?)]
                 (map #(entry-cell row % long?) (range (count columns)))
                 [(all-entries-cell row false long?)])))

;; (entries-row 3 true)

(defn col-header-cell
  [^Integer col]
  (vector :th
          (:name (columns col))))

(defn col-headers-row
  []
  (apply vector (concat [:tr]
                        [[:th]]
                        (map col-header-cell (range (count columns)))
                        [[:th]])))

;; (col-headers-row)

(defn play-column-row
  [^Boolean ttb? ^Boolean long?]
  (apply vector
         (concat [:tr]
                 [[:td]]
                 (map #(vector
                        :td {:class "play-column"
                             :onclick (format
                                       "new Audio('audio/%s%s%s_MP3WRAP.mp3').play();"
                                       (if ttb? "ttb" "btt")
                                       (if long? "long" "short")
                                       ((columns %) (if long? :upper-latin :lower-latin)))}
                        (str "Play " (if ttb? "down" "up")))
                      (range (count columns)))
                 [[:td]])))

;; (play-column-row true true)

(defn table
  [^Boolean long?]
  (apply
   vector
   (concat [:table]
           [(col-headers-row)
            (play-column-row true long?)]
           (map #(entries-row % long?)
                (range
                 (count entries)))
           [(play-column-row false long?)])))

;; (table true)

(defn page
  [title]
  [:html
   [:head
    [:meta {:charset "UTF-8"}]
    [:link {:rel "stylesheet" :href "style.css"}]
    [:title (str title)]]
   [:body
    [:h1 (str title)]
    [:button {:onclick "var l = document.getElementById('long');
                        var s = document.getElementById('short');
                        if (l.style.display == 'none') {
                        l.style.display = 'block';
                        s.style.display = 'none';
                        } else {
                        l.style.display = 'none';
                        s.style.display = 'block';
                        }"} "Toggle short/long"]
    [:div {:id "long"
           :style "display: block;"}
     [:h2 "Long forms"]
     (table true)]
    [:div {:id "short"
           :style "display: none;"}
     [:h2 "Short forms"]
     (table false)]
    [:div {:id "footer"}
     [:p "Made with love by " [:a {:href "mailto:simon@journeyman.cc"} "Simon Brooke"] " for "
      [:img {:id "bug" :src "img/bug.jpg" :alt "Lucy Fyfe"}]]
     [:p "all errors my own"]]]])

(defn tidy-page
  "Reads HTML as a string, emits cleaned-up HTML as a string. This is not yet
   working with HTML as produced by Hiccup, since Hiccup is currently 
   entitifying single quotes within (JavaScript) strings."
  [^String page]
  (let [tidy (Tidy.)
        props (doto (Properties.)
                (.setProperty "char-encoding" "UTF8")
                (.setProperty "indent" "true")
                (.setProperty "input-xml" "false")
                (.setProperty "output-xml" "true")
                (.setProperty "quote-ampersand" "no")
                (.setProperty "smart-indent" "yes"))
        output (StringWriter.)]
    ;; (set! (. config indentContent) true)
    ;; (set! (. config smartIndent) true)
    (doto (.getConfiguration tidy) (.addProps props) (.adjust))
    (.parse tidy (input-stream (.getBytes page))
            output)
    (.toString output)))

(spit "resources/public/index.html"
      (tidy-page
       (s/replace
        (str (html 
              ;;{:escape-strings? false}
              (page "Muharni table")))
        #"\&apos;" "'")))
