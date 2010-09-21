#!/bin/bash

set -e;

cd /data/scratch/whynot2/;
sh run_crawler.sh;
sh run_scripts.sh;
sh run_annotater.sh;
