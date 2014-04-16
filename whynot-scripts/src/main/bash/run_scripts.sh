#!/bin/bash
cd /data/scratch/whynot2/;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister STRUCTUREFACTORS UNANNOTATED\
	| java scripts.ParsePDBEntryType STRUCTUREFACTORS "NMR experiment" "([a-zA-Z0-9]{4})\t.*\tNMR"\
	> comment/SF_NMR_experiment_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister NMR UNANNOTATED\
	| java scripts.ParsePDBEntryType NMR "Diffraction experiment" "([a-zA-Z0-9]{4})\t.*\tdiffraction"\
	> comment/NMR_Diffraction_experiment_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister NMR UNANNOTATED\
	| java scripts.ParsePDBEntryType NMR "Electron microscopy experiment" "([a-zA-Z0-9]{4})\t.*\tEM"\
	> comment/NMR_EM_experiment_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister STRUCTUREFACTORS UNANNOTATED\
	| java scripts.ParsePDBEntryType STRUCTUREFACTORS "Electron microscopy experiment" "([a-zA-Z0-9]{4})\t.*\tEM"\
	> comment/SF_EM_experiment_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister DSSP UNANNOTATED\
	| java scripts.ParsePDBEntryType DSSP "Nucleic acids only" "([a-zA-Z0-9]{4})\tnuc\t.*"\
	> comment/DSSP_Nucleic_acids_only_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister DSSP UNANNOTATED\
	| java scripts.ParsePDBEntryType DSSP "Carbohydrates only" "([a-zA-Z0-9]{4})\tcarb\t.*"\
	> comment/DSSP_Carbohydrates_only_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister DSSP UNANNOTATED\
	| java scripts.GetDSSPErrors > comment/DSSPCMBI_Errors_$(date +%y%m%d).txt;

java -cp dependency/*:whynot-apps.jar nl.ru.cmbi.whynot.list.Lister HSSP UNANNOTATED\
    | ./getHSSPErrors.py > comment/HSSP_Errors_$(date +%y%m%d).txt;

find comment/ -size 0 -print0|xargs -0 rm;
