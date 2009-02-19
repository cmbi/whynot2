CREATE OR REPLACE FUNCTION PDBIDsWithComment(integer) RETURNS SETOF PDBIDS AS '
	SELECT DISTINCT pdbid FROM ENTRYPROPERTYCOMMENTS
	WHERE comment = $1;
' LANGUAGE SQL
STABLE;

/* Entries in this db*/
CREATE OR REPLACE FUNCTION PDBIDsInDB(varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid
	FROM entries
	WHERE database = $1;
' LANGUAGE SQL
STABLE;

/* Entries both parent and in this db */
CREATE OR REPLACE FUNCTION PDBIDsInDBNotObsolete(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid
	FROM entries
	WHERE database = $2
	INTERSECT
		SELECT pdbid
		FROM entries
		WHERE database = $1;
' LANGUAGE SQL
STABLE;

/* Entries in parent but not in this db */
CREATE OR REPLACE FUNCTION PDBIDsNotInDB(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid
	FROM entries
	WHERE database = $2
	EXCEPT
		SELECT pdbid
		FROM entries
		WHERE database = $1;
' LANGUAGE SQL
STABLE;

/* Entries in parent but not in this db, with comment */
CREATE OR REPLACE FUNCTION PDBIDsNotInDBWithComment(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid 
	FROM PDBIDsNotInDB($1,$2)
	INTERSECT 
		SELECT DISTINCT pdbid
		FROM entrypropertycomments
		WHERE database = $1
		AND property = \'Exists\';
' LANGUAGE SQL
STABLE;

/* Entries in parent but not in this db, without comment */
CREATE OR REPLACE FUNCTION PDBIDsNotInDBWithoutComment(varchar(50), varchar(50)) RETURNS SETOF PDBIDS AS '
	SELECT pdbid
	FROM PDBIDsNotInDB($1,$2)
	EXCEPT 
		SELECT DISTINCT pdbid
		FROM entrypropertycomments
		WHERE database = $1
		AND property = \'Exists\';
' LANGUAGE SQL
STABLE;

CREATE OR REPLACE FUNCTION SETDATABASESTATS(varchar(50), varchar(50)) RETURNS INTEGER AS '
	UPDATE DATABASES SET indball = 		(select count(*) from PDBIDsInDB($1))::INTEGER 				WHERE name = $1;
	UPDATE DATABASES SET indbcorrect = 	(select count(*) from PDBIDsInDBNotObsolete($1,$2))::INTEGER 		WHERE name = $1;
	UPDATE DATABASES SET indbincorrect = 	(select count(*) from PDBIDsNotInDB($2,$1))::INTEGER 			WHERE name = $1;
	UPDATE DATABASES SET notindball = 	(select count(*) from PDBIDsNotInDB($1,$2))::INTEGER 			WHERE name = $1;
	UPDATE DATABASES SET notindbcorrect = 	(select count(*) from PDBIDsNotInDBWithComment($1,$2))::INTEGER 	WHERE name = $1;
	UPDATE DATABASES SET notindbincorrect = (select count(*) from PDBIDsNotInDBWithoutComment($1,$2))::INTEGER 	WHERE name = $1;
	SELECT 1;
' LANGUAGE SQL;
