(ns muharni.construct
  "Construct 'Muharni' tables used in learning Gurmukhi script, a script
   devised and tradiitionally used for Sikh religious texts."
  (:require [hiccup.core :refer [html]]
            [clojure.java.io :refer [input-stream]]
            [clojure.string :as s])
  (:import [java.io StringWriter]
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
  [{:name "Muktā", :punjabi "ਮੁਕਤਾ", :lower-latin "b", :upper-latin "B"}
   {:name "Kannā", :punjabi "ਕੰਨਾ", :lower-latin "c", :upper-latin "C"}
   {:name "Sihārī", :punjabi "ਸਿਹਾਰੀ", :lower-latin "d", :upper-latin "D"}
   {:name "Bihārī", :punjabi "ਬਿਹਾਰੀ", :lower-latin "e", :upper-latin "E"}
   {:name "Auṅkaṛ", :punjabi "ਔਂਕੜ", :lower-latin "f", :upper-latin "F"}
   {:name "Dulaiṅkaṛ", :punjabi "ਦੁਲੈਂਕੜ", :lower-latin "g", :upper-latin "G"}
   {:name "Lāvā", :punjabi "ਲਾਂਵਾਂ", :lower-latin "h", :upper-latin "H"}
   {:name "Dulāvā", :punjabi "ਦੁਲਾਂਵਾਂ", :lower-latin "i", :upper-latin "I"}
   {:name "Hōṛā", :punjabi "ਹੋੜਾ", :lower-latin "j", :upper-latin "J"}
   {:name "Kanauṛā", :punjabi "ਕਨੌੜਾ", :lower-latin "k", :upper-latin "K"}
   {:name "Ṭippī", :punjabi "ਟਿੱਪੀ", :lower-latin "l", :upper-latin "L"}
   {:name "Bindī", :punjabi "ਬਿੰਦੀ", :lower-latin "m", :upper-latin "M"}])

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
  (let [r (inc row)
        c ((columns col) (if long? :upper-latin :lower-latin))
        audio (format "audio/%02d%s.mp3" r c)
        char ((entries row) col)]
    (vector :td {:class "entry"}
            [:span {:class "entry-text"
                    :id (format "%s%02d%s" (if long? "l" "s") r c)} char]
            [:br]
            [:button
             {:onclick (str "new Audio('" audio "').play();")}
             "&#9658;"])))

;; (entry-cell 3 4 true)

(defn all-entries-cell
  "Return a table cell which plays the sound for all entries in the specified 
   `row`, concatenated from left to right if `ltr?` is true else right to left,
   and playing long recordings if `long?` is true else short recordings."
  [^Integer row ^Boolean ltr? ^Boolean long?]
  [:td {:class "play-row"
        :onclick (format "new Audio('audio/%s%s%02d_MP3WRAP.mp3').play();"
                         (if ltr? "ltr" "rtl")
                         (if long? "long" "short")
                         (inc row))}
   (str "Play " (if ltr? "LTR" "RTL"))])


(defn entries-row
  "Return a table row for the specified `row` number for long recordings if 
   `long?` is true else short recordings."
  [^Integer row ^Boolean long?]
  (apply vector
         (concat [:tr]
                 [(all-entries-cell row true long?)]
                 (map #(entry-cell row % long?) (range (count columns)))
                 [(all-entries-cell row false long?)])))

;; (entries-row 3 true)

(defn col-header-cell
  "Return a header cell for the indicated `column`."
  [^Integer column ^Boolean punjabi?]
  (vector :th
          ((if punjabi? :punjabi :name) (columns column))))

(defn col-headers-row
  [^Boolean punjabi?]
  (apply vector (concat [:tr]
                        [[:th]]
                        (map #(col-header-cell % punjabi?) (range (count columns)))
                        [[:th]])))

;; (col-headers-row)

(defn play-column-row
  "Return a table row of cells which play concatenated sounds for each column,
   concatenating from top to bottom is `ttb?` is true, else bottom to top, and
   playing long recordings if `long?` is true else short recordings."
  [^Boolean ttb? ^Boolean long?]
  (apply vector
         (concat [:tr]
                 [[:td]]
                 (map #(vector
                        :td {:class "play-column"
                             :onclick
                             (format
                              "new Audio('audio/%s%s%s_MP3WRAP.mp3').play();"
                              (if ttb? "ttb" "btt")
                              (if long? "long" "short")
                              ((columns %) (if long? :upper-latin :lower-latin)))}
                        (str "Play " (if ttb? "down" "up")))
                      (range (count columns)))
                 [[:td]])))

;; (play-column-row true true)

(defn table
  "Lay out a muharni table, playing long recordings if `long?` is true else 
   short recordings."
  [^Boolean long?]
  (apply
   vector
   (concat [:table {:class "character-table"
                    :summary "Table of Punjabi characters from which to select sound recordings"}]
           [(col-headers-row true)
            (col-headers-row false)
            (play-column-row true long?)]
           (map #(entries-row % long?)
                (range
                 (count entries)))
           [(play-column-row false long?)])))

;; (table true)

(defn page
  "Construct the complete muharni tables page."
  [title]
  [:html
   [:head
    [:meta {:charset "UTF-8" :content ""}]
    [:link {:rel "stylesheet" :type "text/css" :href "style.css"}]
    ;; pull jquery from Google rather than host locally.
    [:script {:type "text/javascript"
              :src "https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"}]
    [:script {:type "text/javascript"
              :src "scripts/muharni.js"}]
    [:title (str title)]]
   [:body {:id "body"}
    [:div {:id "popup"
           :style "display: none; border: thin solid gray; width: 10%"}
     [:div {:id "closebox"
            :onclick "$('#popup').hide();"} "&#10006;"]
     [:p {:id "character" :style "text-align: center; margin: 0; font-size: 4em;"} "?"]
     [:table {:id "controls" :summary "Controls for audio playback and recording"}
      [:tr
       [:th "Tutor"]
       [:td  [:span {:id "play-tutor"}
              "&#9658;"]]]
      [:tr
       [:th "You"]
       [:td  [:span {:id "play-student"} "&#9658;"]]
       [:td  [:span {:id "record-stop"} "&#9210;"]]]
      [:tr
       [:td {:colspan 3 :id "progress"}]]]]
    [:h1 (str title)]
    [:button {:onclick "var l = document.getElementById('long');
                        var s = document.getElementById('short');
                        if (l.style.display == 'none') {
                        l.style.display = 'block';
                        s.style.display = 'none';
                        } else {
                        l.style.display = 'none';
                        s.style.display = 'block';
                        }
                        $('#popup').hide();"} "Toggle short/long"]
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
     [:p "all errors my own."]
     [:p [:img {:height 16 :width 16 :alt "GitHub logo" :src "img/github-logo-transparent.png"}]
      "Find me/fork me on "
      [:a {:href "https://github.com/simon-brooke/muharni"} "GitHub"]]]]])

(defn tidy-page
  "Reads HTML from `page` as a string, returns cleaned-up HTML as a string."
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
    ;;  NOTE: Hiccup is currently entitifying single quotes within 
    ;; (JavaScript) strings. This is NOT desirable behaviour!
    (.parse tidy (input-stream (.getBytes (s/replace page #"\&apos;" "'")))
            output)
    (str "<!DOCTYPE html>\n" (.toString output))))

(spit "resources/public/index.html"
      (tidy-page
       (str (html
             (page "Muharni table")))))
