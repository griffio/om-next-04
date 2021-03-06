# om-next-04

Simple example based around [om-next](https://github.com/omcljs/om/wiki/Quick-Start-%28om.next%29) 

**This is not an example of how remote hosts work**.

Experimenting with how/why "temp ids" work by default.

Use a temp-id generated in client and then updated by 'remote', :tempids merged back into application state.

Shows extracting query expression from om.next/query->ast.

[1.0.0-alpha47](https://clojars.org/org.omcljs/om)

## Overview

![Figwheel Idea Cursive](https://raw.githubusercontent.com/griffio/griffio.github.io/master/public/om-next-04.gif)

## Setup

~~~
lein run -m clojure.main script/repl.clj
~~~

Or

Intellij - Cursive - REPL

![Figwheel Idea Cursive](https://raw.githubusercontent.com/griffio/griffio.github.io/master/public/figwheel-idea.png)

Open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2016

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
