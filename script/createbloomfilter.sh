#!/bin/bash

java -cp 'CrawlerEngine.jar:lib/*' bit.crawl.cmdline.CmdlineBloomFilterRunner "$@"
