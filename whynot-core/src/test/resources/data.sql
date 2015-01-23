-- Two databanks
insert into databank (id, crawltype, filelink, name, parent_id, reference, regex) values (1, 'LINE', '#${PDBID}', 'PDB', 1, 'https://pdb.org', '[a-zA-Z0-9]{4}');
insert into databank (id, crawltype, filelink, name, parent_id, reference, regex) values (2, 'LINE', '#${PDBID}', 'DSSP', 1, 'https://dssp.org', '[a-zA-Z0-9]{4}');

-- One file
insert into file (id, path, timestamp) values (3, '/tmp/1crn.pdb', 123456789);

-- Three parent entries where DSSP is missing
-- One where child is annotated
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '0crn', 3);
-- Two where child is unannotated
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '1crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '2crn', 3);

-- Eight parent entries where DSSP is present
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '3crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '4crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '5crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '6crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '7crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '8crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '9crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 1, '10crn', 3);

-- Twelve present entries:
-- Eight valid entries
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '3crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '4crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '5crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '6crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '7crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '8crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '9crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '10crn', 3);
-- Four obsolete entries
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '11crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '12crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '13crn', 3);
insert into entry (id, databank_id, pdbid, file_id) values (default, 2, '14crn', 3);

-- One annotated entry
insert into entry (id, databank_id, pdbid, file_id) values (-4, 2, '0crn', null);
insert into comment (id, text) values (5, 'Some comment');
insert into annotation (id, entry_id, comment_id, timestamp) values (default, -4, 5, '234567890');
