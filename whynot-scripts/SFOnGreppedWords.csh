#!/bin/csh -f

# missingSF.csh: makes WHY_NOT file for missing SF files.
#
# This script was created by Robbie Joosten (r.joosten@cmbi.ru.nl, robbie_joosten@hotmail.com)


#Set environment parameters (define directories and files). Edit when needed.
set LISTING = "./SF_UN.sh"
setenv COMDIR $PWD 						#Target directory for WHY_NOT comments
setenv PDB /data/uncompressed/pdb 		#Directory with decompressed pdb files

#Usually no need to edit these.
set DATE = `date +'%G%m%d'` 			#Datestamp for comment file
setenv OUTFIL $COMDIR/${DATE}_SF.txt 	#Comment output file

#Loop over missing SF files
foreach PDBID (`$LISTING`)
	#Could experimental data exist?
	if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'DIFFRACTION'` != 0) then
		echo "${PDBID}: Diffraction experiment. No reason for missing data."
	else if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'ELECTRON'` != 0) then
		echo "${PDBID}: Electron microscopy/diffraction experiment. No reason for missing data."
	else if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'SCATTERING'` != 0) then
		echo "${PDBID}: Scattering will be ignored for now."
	else if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'NMR'` != 0) then
		echo "${PDBID}: NMR experiment. Cannot have structure factors."
		echo "COMMENT: NMR experiment" 						>> $OUTFIL #Create whynot entry
		echo "STRUCTUREFACTORS,$PDBID"						>> $OUTFIL
	else if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'FLUORESCENCE TRANSFER'` != 0) then
		echo "${PDBID}: Fluorescence transfer experiment. Cannot have structure factors."
		echo "COMMENT: Fluorescence transfer experiment" 	>> $OUTFIL #Create whynot entry
		echo "STRUCTUREFACTORS,$PDBID"						>> $OUTFIL
	else if (`grep ^EXPDTA $PDB/pdb$PDBID.ent | head -n 1 | grep -c 'INFRARED SPECTROSCOPY'` != 0) then
		echo "${PDBID}: Infrared spectroscopy experiment. Cannot have structure factors."
		echo "COMMENT: Infrared spectroscopy experiment" 	>> $OUTFIL #Create whynot entry
		echo "STRUCTUREFACTORS,$PDBID"						>> $OUTFIL
	else
		echo "${PDBID}: Unknown experiment type"
	endif
end
