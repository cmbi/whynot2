#!/bin/bash
cd /data/scratch/whynot2/;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.annotate.Annotater;
