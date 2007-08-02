package persistance;

import java.sql.SQLException;

import model.Comment;
import model.Database;
import model.Entry;
import model.PDBID;
import model.Property;
import persistance.connect.Connector;
import persistance.connect.DBConnectionException;
import persistance.functions.DBStats;
import persistance.functions.Functions;
import persistance.tables.Comments;
import persistance.tables.Databases;
import persistance.tables.Entries;
import persistance.tables.EntryPropertyComments;
import persistance.tables.PDBIDS;
import persistance.tables.Properties;

public class RDB implements IPersistance {

	public RDB(java.util.Properties prop) throws DBConnectionException {
		Connector.connect(prop);
	}

	// CleanUp
	public boolean cleanUp() {
		//Update calculated number of entries in various collections
		try {
			for (Database db : Databases.getMultiple("%"))
				Functions.CalculateDatabaseStats(db.getName());
		} catch (SQLException e1) {	e1.printStackTrace();}

//		//VACUUM FULL ANALYZE (enables queryplanner to optimize queries)
//		try {
//			Connector.getConnection().createStatement().execute(
//					"VACUUM FULL ANALYZE;");
//		} catch (SQLException e) { e.printStackTrace();}
		return true;
	}

	// CleanUp
	public boolean cleanUp(String database) {
		//Update calculated number of entries in various collections
		try {
			Database db = Databases.getSingle(database);
			Functions.CalculateDatabaseStats(db.getName());
		} catch (SQLException e1) {e1.printStackTrace();}

		//VACUUM FULL ANALYZE (enables queryplanner to optimize queries)
//		try {
//			return Connector.getConnection().createStatement().execute(
//					"VACUUM FULL ANALYZE;");
//		} catch (SQLException e) {
//			return false;
//		}
		return true;
	}

	// Crawler
	public boolean addEntry(Entry msdfile) {
		return Entries.persist(msdfile);
	}

	public boolean removeEntry(Entry msdfile) {
		return Entries.remove(msdfile);
	}

	public Entry[] getEntriesForDatabase(Database db)
			throws PersistanceException {
		try { // FIXME: Find out if this array can become too large and break
			// the app.. if so return an Iterator
			return Entries.getMultiple(db.getName(), "%");
		} catch (SQLException e) {
			throw new PersistanceException("getEntriesForDatabase", e);
		}
	}

	// Commenter
	public PDBID getPDBID(String pdbid) {
		try {
			return PDBIDS.getSingle(pdbid);
		} catch (SQLException e) {
			return null;
		}
	}

	public Database getDatabase(String database) {
		try {
			return Databases.getSingle(database);
		} catch (SQLException e) {
			return null;
		}
	}

	public Property getProperty(String property) {
		try {
			return Properties.getSingle(property);
		} catch (SQLException e) {
			return null;
		}
	}

	public Comment getComment(String comment) {
		try {
			return Comments.getSingle(comment);
		} catch (SQLException e) {
			return null;
		}
	}

	public String[][] getCommentRecords() {
		try {
			return EntryPropertyComments.getCommentRecords();
		} catch (SQLException e) {
			return null;
		}

	}

	public boolean addCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, Comment comment) {
		return EntryPropertyComments.persist(pdbid, db, prop, comment);
	}

	public boolean addCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, boolean bool, Comment comment) {
		return EntryPropertyComments.persist(pdbid, db, prop, bool, comment);
	}

	public boolean removeCommentForPropertyOfEntry(PDBID pdbid, Database db,
			Property prop, Comment comment) {
		return EntryPropertyComments.remove(pdbid, db, prop, comment);
	}

	// Web
	public String getParentDatabase(String database) {
		return Databases.getParent(database);
	}

	public Database[] getDatabases() throws PersistanceException {
		try {
			return Databases.getMultiple("%");
		} catch (SQLException e) {
			throw new PersistanceException("getDatabases", e);
		}
	}

	public Entry getEntry(String pdbid, String database) {
		try {
			return Entries.getSingle(database, pdbid);
		} catch (SQLException e) {
			return null;
		}
	}

	public Comment getComment(int comid) {
		try {
			return Comments.getSingle(comid);
		} catch (SQLException e) {
			return null;
		}
	}

	public Comment[] getComments() throws PersistanceException {
		try {
			return Comments.getMultiple("%");
		} catch (SQLException e) {
			throw new PersistanceException("getComments", e);
		}
	}

	public Comment[] getCommentsForPropertyOfEntry(String pdbid,
			String database, String property) throws PersistanceException {
		try {
			return EntryPropertyComments.getCommentsForPropertyOfEntry(
					database, pdbid, property);
		} catch (SQLException e) {
			throw new PersistanceException("getCommentsForPropertyOfEntry", e);
		}
	}

	public PDBID[] getPDBIDsInDB(String childdatabase)
			throws PersistanceException {
		try {
			return Functions.PDBIDsInDB(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsInDB", e);
		}
	}

	public PDBID[] getPDBIDsInDBNotObsolete(String childdatabase) throws PersistanceException {
		try {
			return Functions.PDBIDsInDBNotObsolete(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsInDBNotObsolete", e);
		}
	}

	public PDBID[] getPDBIDsInDBObsolete(String childdatabase) throws PersistanceException {
		try {
			return Functions.PDBIDsInDBObsolete(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsInDBNotObsolete", e);
		}
	}

	public PDBID[] getPDBIDsNotInDB(String childdatabase)
			throws PersistanceException {
		try {
			return Functions.PDBIDsNotInDB(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsNotInDB", e);
		}
	}

	public PDBID[] getPDBIDsNotInDBWithComment(String childdatabase) throws PersistanceException {
		try {
			return Functions.PDBIDsNotInDBWithComment(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsNotInDBWithComment", e);
		}
	}

	public PDBID[] getPDBIDsNotInDBWithoutComment(String childdatabase) throws PersistanceException {
		try {
			return Functions.PDBIDsNotInDBWithoutComment(childdatabase);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsNotInDBWithoutComment", e);
		}
	}

	public PDBID[] getPDBIDsWithComment(int comid) throws PersistanceException {
		try {
			return Functions.PDBIDsWithComment(comid);
		} catch (SQLException e) {
			throw new PersistanceException("getPDBIDsWithComment", e);
		}
	}

	public DBStats getDBStats(String childdb) throws PersistanceException {
		try {
			return Databases.getDBStats(childdb);
		} catch (SQLException e) {
			throw new PersistanceException("getDBStats", e);
		}
	}

}
