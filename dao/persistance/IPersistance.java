package persistance;

import model.Comment;
import model.Database;
import model.Entry;
import model.PDBID;
import model.Property;
import persistance.functions.DBStats;
import persistance.functions.RDBStats;

/**
 * Templates: Object getObject(String id); > returns Object or null Object[]
 * getObjects() throws Exception; > returns Object[] (possibly empty) boolean
 * addObject(Object o); > returns true or false public boolean
 * removeObject(Object o); > returns true or false
 */
public interface IPersistance {

	// DAO
	public boolean cleanUp();

	public boolean cleanUp(String database);

	// Crawler
	public boolean addEntry(Entry entry);

	public boolean removeEntry(Entry entry);

	public Entry[] getEntriesForDatabase(Database db)
			throws PersistanceException;

	// Commenter
	public PDBID getPDBID(String pdbid);

	public Database getDatabase(String database);

	public Property getProperty(String property);

	public Comment getComment(String comment);

	public String[][] getCommentRecords();

	public boolean addCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, Comment com);

	public boolean addCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, boolean bool, Comment com);

	public boolean removeCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, Comment com);

	// Web
	public String getParentDatabase(String childdatabase);

	public Database[] getDatabases() throws PersistanceException;

	public Entry getEntry(String pdbid, String database);

	public Comment getComment(int comid);

	public Comment[] getComments() throws PersistanceException;

	public Comment[] getCommentsForPropertyOfEntry(String pdbid,
			String database, String property) throws PersistanceException;

	public PDBID[] getPDBIDsInDB(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsInDBNotObsolete(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsInDBObsolete(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsNotInDB(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsNotInDBWithComment(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsNotInDBWithoutComment(String childdatabase)
			throws PersistanceException;

	public PDBID[] getPDBIDsWithComment(int comid)
			throws PersistanceException;

	public DBStats getDBStats(String childdatabase) throws PersistanceException;

	public RDBStats getRDBStats() throws PersistanceException;
}
