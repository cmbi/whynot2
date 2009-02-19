/* All following SQL statements should be executed
 - on database whynot */
SET SESSION AUTHORIZATION 'whynot_owner';

CREATE SCHEMA public AUTHORIZATION whynot_owner;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO whynot_owner;

SET search_path TO public;

CREATE TABLE AUTHORS (
   NAME VARCHAR(50) NOT NULL,
   EMAIL VARCHAR(50) NOT NULL,

   PRIMARY KEY (NAME)
);

CREATE TABLE COMMENTS (
   COMID SERIAL NOT NULL,
   AUTHOR VARCHAR(50) NOT NULL,
   COMMENT TEXT NOT NULL,
   TIMESTAMP TIMESTAMP NOT NULL,

   PRIMARY KEY (COMID)
);

CREATE TABLE DATABASES (
   NAME VARCHAR(50) NOT NULL,
   REGEX VARCHAR(50) NOT NULL,
   PDBIDOFFSET INTEGER NOT NULL,
   REFERENCE TEXT NULL,
   FILELINK TEXT NULL,

   INDBALL INTEGER NULL,
   INDBCORRECT INTEGER NULL,
   INDBINCORRECT INTEGER NULL,
   NOTINDBALL INTEGER NULL,
   NOTINDBCORRECT INTEGER NULL,
   NOTINDBINCORRECT INTEGER NULL,

   PRIMARY KEY (NAME)
);

CREATE TABLE ENTRIES (
   DATABASE VARCHAR(50) NOT NULL,
   PDBID VARCHAR(4) NOT NULL,
   FILEPATH VARCHAR(200) NOT NULL,
   TIMESTAMP TIMESTAMP NOT NULL,

   PRIMARY KEY (PDBID,DATABASE)
);

CREATE TABLE ENTRYPROPERTIES (
   DATABASE VARCHAR(50) NOT NULL,
   PDBID VARCHAR(4) NOT NULL,
   PROPERTY VARCHAR(50) NOT NULL,
   BOOLEAN BOOLEAN NOT NULL,

   PRIMARY KEY (PDBID, PROPERTY, DATABASE)
);

CREATE TABLE ENTRYPROPERTYCOMMENTS (
   DATABASE VARCHAR(50) NOT NULL,
   PDBID VARCHAR(4) NOT NULL,
   PROPERTY VARCHAR(50) NOT NULL,
   COMMENT INTEGER NOT NULL,

   PRIMARY KEY (PDBID, PROPERTY, COMMENT, DATABASE)
);

CREATE TABLE PDBIDS (
   PDBID VARCHAR(4) NOT NULL,

   PRIMARY KEY (PDBID)
);

CREATE TABLE PROPERTIES (
   NAME VARCHAR(50) NOT NULL,
   EXPLANATION TEXT,

   PRIMARY KEY (NAME)
);

/* Alter all tables to add foreign keys and implicit indices */
ALTER TABLE COMMENTS
   ADD FOREIGN KEY (AUTHOR)
   REFERENCES AUTHORS (NAME);

ALTER TABLE ENTRIES
   ADD FOREIGN KEY (DATABASE)
   REFERENCES DATABASES (NAME);

ALTER TABLE ENTRIES
   ADD FOREIGN KEY (PDBID)
   REFERENCES PDBIDS (PDBID);

ALTER TABLE ENTRYPROPERTIES
   ADD FOREIGN KEY (DATABASE)
   REFERENCES DATABASES (NAME);

ALTER TABLE ENTRYPROPERTIES
   ADD FOREIGN KEY (PROPERTY)
   REFERENCES PROPERTIES (NAME);

ALTER TABLE ENTRYPROPERTIES
   ADD FOREIGN KEY (PDBID)
   REFERENCES PDBIDS (PDBID);

ALTER TABLE ENTRYPROPERTYCOMMENTS
   ADD FOREIGN KEY (DATABASE, PDBID, PROPERTY)
   REFERENCES ENTRYPROPERTIES (DATABASE, PDBID, PROPERTY);

ALTER TABLE ENTRYPROPERTYCOMMENTS
   ADD FOREIGN KEY (COMMENT)
   REFERENCES COMMENTS (COMID);

/* Create explicit alternative indices on selected tables for increased performance */
CREATE UNIQUE INDEX comments_altkey ON COMMENTS USING btree (comment);
CREATE UNIQUE INDEX entries_altkey ON ENTRIES USING btree (database, pdbid);

/* Create functions for often reused queries */
CREATE FUNCTION PDBIDsInDB(varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid
	FROM entries
	WHERE database = $1
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION PDBIDsNotInDB(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT a.pdbid
	FROM PDBIDsInDB($2) a
	EXCEPT
		SELECT b.pdbid
		FROM PDBIDsInDB($1) b
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION PDBIDsNotInDBWithComment(varchar(50),varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT * FROM PDBIDsNotInDB($1,$2)
	WHERE pdbid IN (
		SELECT DISTINCT pdbid
		FROM entrypropertycomments
		WHERE database = $1
		AND property = \'Exists\')
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION PDBIDsNotInDBWithoutComment(varchar(50),varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT * FROM PDBIDsNotInDB($1,$2)
	WHERE pdbid NOT IN (
		SELECT DISTINCT pdbid
		FROM entrypropertycomments
		WHERE database = $1
		AND property = \'Exists\')
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION PDBIDsInDBNotObsolete(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT a.pdbid
	FROM
		(SELECT pdbid FROM PDBIDsInDB($1)) a,
		(SELECT pdbid FROM PDBIDsInDB($2)) b
	WHERE a.pdbid = b.pdbid
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION PDBIDsWithComment(integer) RETURNS SETOF PDBIDS AS '
	SELECT DISTINCT pdbid FROM ENTRYPROPERTYCOMMENTS
	WHERE comment = $1
	ORDER BY pdbid;
' LANGUAGE SQL;

CREATE FUNCTION SETDATABASESTATS(varchar(50),varchar(50)) RETURNS INTEGER AS '
	UPDATE DATABASES SET indball = 		(select count(*) from PDBIDsInDB($1))::INTEGER 				WHERE name = $1;
	UPDATE DATABASES SET indbcorrect = 	(select count(*) from PDBIDsInDBNotObsolete($1,$2))::INTEGER 		WHERE name = $1;
	UPDATE DATABASES SET indbincorrect = 	(select count(*) from PDBIDsNotInDB($2,$1))::INTEGER 			WHERE name = $1;
	UPDATE DATABASES SET notindball = 	(select count(*) from PDBIDsNotInDB($1,$2))::INTEGER 			WHERE name = $1;
	UPDATE DATABASES SET notindbcorrect = 	(select count(*) from PDBIDsNotInDBWithComment($1,$2))::INTEGER 	WHERE name = $1;
	UPDATE DATABASES SET notindbincorrect = (select count(*) from PDBIDsNotInDBWithoutComment($1,$2))::INTEGER 	WHERE name = $1;
	SELECT 1;
' LANGUAGE SQL;