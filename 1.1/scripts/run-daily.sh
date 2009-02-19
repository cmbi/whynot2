#!/bin/bash
cd /opt/whynot/;

datum=$(date +%Y%m%d)

CRAWLFILE="logs/crawler-$datum.log";
COMMENTFILE="logs/commenter-$datum.log";

./crawler.sh &> $CRAWLFILE;
./commenter.sh &> $COMMENTFILE;

echo "Done.";
