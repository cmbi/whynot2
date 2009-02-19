package nl.ru.cmbi.whynot.jsp.whynot;

import model.Database;
import model.PDBID;
import nl.ru.cmbi.whynot.all.MyActionSupport;

import org.apache.struts2.ServletActionContext;

import persistance.PersistanceException;
import persistance.functions.DBStats;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class Reports extends MyActionSupport {
	public static final String ENTRY = "entry";

	public static final String DBSTATS = "dbstats";

	@Override
	public String execute() {
		Database[] databases = null;
		try { databases = this.getRDB().getDatabases(); }
		catch (PersistanceException e) {}
		if (databases == null)
			this.addActionError(this.getText("rdb.error.databases"));
		this.setDatabases(databases);

		if (this.hasErrors())
			return Action.ERROR;
		return Action.SUCCESS;
	}

	public String getDatabaseStats() {
		String sdatabase = this.getSdatabase();
		try {
			DBStats dbstats = this.getRDB().getDBStats(sdatabase);
			ServletActionContext.getRequest().setAttribute(Reports.DBSTATS,dbstats);
		}
		catch (PersistanceException e) {}
		return "done";
	}

	//#####  #####

	private String getParentDatabase(String childdatabase) {
		return this.getRDB().getParentDatabase(childdatabase);
	}

	public String indball(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsInDB(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("All PDBIDs in " + childdatabase + ".");
		return "collection";
	}

	public String indbcorrect(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsInDBNotObsolete(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("Correct PDBIDs in " + childdatabase + ".");
		return "collection";
	}

	public String indbincorrect(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsInDBObsolete(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("Obsolete PDBIDs in " + childdatabase + ".");
		return "collection";
	}

	public String notindball(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsNotInDB(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("All PDBIDs in " + this.getParentDatabase(childdatabase) + " but not in " + childdatabase + ".");
		return "collection";
	}

	public String notindbcorrect(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsNotInDBWithComment(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("All PDBIDs in " + this.getParentDatabase(childdatabase) + " but not in " + childdatabase + " with a comment.");
		return "collection";
	}

	public String notindbincorrect(){
		String childdatabase = this.getSdatabase();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsNotInDBWithoutComment(childdatabase);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("All PDBIDs in " + this.getParentDatabase(childdatabase) + " but not in " + childdatabase + " without a comment.");
		return "collection";
	}
}
