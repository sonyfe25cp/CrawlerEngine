#! /bin/sh

ant -f build-jar.xml 

cd build/dist/crawlerengine/

sudo chmod +x bin/runcrawl.sh

bin/runcrawl.sh real-world-tasks/newsgn-qq-new.spring.xml

