#!/bin/bash
cd /data/scratch/whynot2/;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler PDB /data/raw/pdb/;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler PDBFINDER /data/raw/pdbfinder/PDBFIND.TXT;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler PDBFINDER2 /data/raw/pdbfinder2/PDBFIND2.TXT;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler PDBREPORT /data/raw/pdbreport;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler DSSP /data/uncompressed/dssp/;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler HSSP /data/uncompressed/hssp/;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler RECOORD http://www.ebi.ac.uk/pdbe/docs/NMR/recoord/entryList.txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler NMR http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nmr.csv;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler NRG http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg.csv;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler NRG-DOCR http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg_docr.csv;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler NRG-CING http://nmr.cmbi.ru.nl/NRG-CING/entry_list_done.csv;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler STRUCTUREFACTORS /data/uncompressed/structure_factors/;
java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.crawl.Crawler PDB_REDO /data/raw/pdb_redo/;
