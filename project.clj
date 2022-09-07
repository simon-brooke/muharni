(defproject muharni "0.1.0-SNAPSHOT"
  :description "Format a web page for students of muharni"
  :url "https://www.journeyman.cc/muharni/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clj-http "3.12.3"]
                 [enlive "1.1.6"]
                 [hiccup "1.0.5"]
                 [hickory "0.7.1"]
                 [com.github.jtidy/jtidy "1.0.2"]]
  :repl-options {:init-ns muharni.construct})
