#!/usr/bin/python

import sys

queryIDs=[]
for arg in sys.argv[1:]:
	queryIDs.append(arg.upper())

for line in sys.stdin:
	queryIDs.extend(line.upper().split())

entryID=None
sequences=[]
for line in open('/data/pdbfinder2/PDBFIND2.TXT','r'):

	if line=='\n':

		continue

	elif line.startswith('//'):

		if entryID in queryIDs:

			npep=0
			nlong=0
			for seq in sequences:
				if seq.replace('X','').isupper():
					npep+=1
					if len(seq)>=25:
						nlong+=1
			if npep<=0:
			  	print 'COMMENT: No peptide sequences found\nHSSP,%s'%entryID
			elif nlong<=0:
				print 'COMMENT: Peptide sequences are all shorter than 25 AA\nHSSP,%s'%entryID

		entryID=None
		sequences=[]

		continue

	i=line.find(':')
	if i<0:
		continue
	key=line[:i].strip()
	value=line[i+1:].strip()

	if key=='ID':

		entryID=value

	elif key=='Sequence':

		sequences.append(value)
