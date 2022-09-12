# muharni

A Clojure library designed to hack up a web page for studying the Punjabi
script and the pronunciation thereof approved for the reading of Sikh
sacred texts.

## Usage

In its present state of extreme hackiness, just loading the namespace `muharni.construct` will cause the web page to be generated.

Thus, start the REPL by connecting to the repository directory and invoking

> lein repl

The REPL will start up, and eventually show a prompt 

> muharni.construct=>  

At this prompt, invoke

> (use 'muharni.construct :reload)

You'll see a series of messages which are in fact printed by [JTidy](https://github.com/jtidy/jtidy) and which you can safely ignore. As a side effect, the HTML page will be generated in `resources/public/index.html`.

## License

Copyright © 2022 Lucy Fyfe and Simon Brooke

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
