#!/bin/bash
java -jar whynot-crawler.jar -t PDB -s directory -p /mnt/cmbi8/raw/pdb/;
java -jar whynot-crawler.jar -t DSSP -s directory -p /mnt/cmbi8/uncompressed/dssp/;
java -jar whynot-crawler.jar -t HSSP -s directory -p /mnt/cmbi8/uncompressed/hssp/;
java -jar whynot-crawler.jar -t PDBFINDER -s pdbfinderfile -p /mnt/cmbi8/raw/pdbfinder/PDBFIND.TXT;
java -jar whynot-crawler.jar -t PDBREPORT -s directory -p /mnt/cmbi8/raw/pdbreport/;
java -jar whynot-crawler.jar -t PDB_REDO -s directory -p /mnt/cmbi8/raw/pdb_redo/;
java -jar whynot-crawler.jar -t STRUCTUREFACTORS -s directory -p /mnt/cmbi8/srs/structure_factors/;
