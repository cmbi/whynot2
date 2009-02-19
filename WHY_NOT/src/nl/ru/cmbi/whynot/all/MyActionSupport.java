package nl.ru.cmbi.whynot.all;

import java.util.Map;

import model.Comment;
import model.Database;
import model.PDBID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import persistance.IPersistance;
import persistance.RDB;

import com.opensymphony.xwork2.ActionSupport;

public class MyActionSupport extends ActionSupport implements SessionAware,
		ApplicationAware {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Return CANCEL so apropriate result can be selected.
	 *
	 * @return "cancel" so apropriate result can be selected.
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * <p>
	 * <code>Log</code> instance for this application.
	 * </p>
	 */
	protected Log log = LogFactory.getLog(Constants.PACKAGE);

	// ---- ApplicationAware ----
	private Map application;

	public Map getApplication() {
		return this.application;
	}

	public void setApplication(Map value) {
		this.application = value;
	}

	// ---- SessionAware ----
	private Map session;

	public Map getSession() {
		return this.session;
	}

	public void setSession(Map value) {
		this.session = value;
	}

	// ---- RDB property ----
	public RDB getRDB() {
		Object rdb = this.getApplication().get(Constants.RDB_KEY);
		if (rdb == null)
			this.addActionError(this.getText("error.database.missing"));
		return (RDB) rdb;
	}

	@SuppressWarnings("unchecked")
	public void setRDB(IPersistance rdb) {
		this.getApplication().put(Constants.RDB_KEY, rdb);
	}

	// ---- scomid property ----
	private String scomid = null;

	public String getScomid() {
		return this.scomid;
	}

	public void setScomid(String value) {
		this.scomid = value;
	}

	// ---- sdetail property ----
	private String sdetail = null;

	public String getSdetail() {
		return this.sdetail;
	}

	public void setSdetail(String value) {
		this.sdetail = value;
	}

	// ---- spdbid property ----
	private String spdbid = null;

	public String getSpdbid() {
		return this.spdbid;
	}

	public void setSpdbid(String value) {
		if (value.matches("[\\d\\w]{4}"))
			this.spdbid = value.toLowerCase();
		else
			if (value.matches("") || value == null)
				this.addActionError(this
					.getText("form.error.validation.spdbid_missing"));
			else
				this.addActionError(this
					.getText("form.error.validation.spdbid_format"));
	}

	// ---- PDBID property ----
	private PDBID pdbid = null;

	public PDBID getPDBID() {
		return this.pdbid;
	}

	public void setPDBID(PDBID value) {
		this.pdbid = value;
	}

	// ---- sdatabase property ----
	private String sdatabase = null;

	public String getSdatabase() {
		return this.sdatabase;
	}

	public void setSdatabase(String value) {
		this.sdatabase = value;
	}

	// ---- Comments property ----
	private Comment[] comments = null;

	public Comment[] getComments() {
		return this.comments;
	}

	public void setComments(Comment[] value) {
		this.comments = value;
	}

	// ---- Databases property ----
	private Database[] databases = null;

	public Database[] getDatabases() {
		return this.databases;
	}

	public void setDatabases(Database[] value) {
		this.databases = value;
	}

	// ---- sproperty property ----
	private String sproperty = null;

	public String getSproperty() {
		return this.sproperty;
	}

	public void setSproperty(String value) {
		this.sproperty = value;
	}

	// ---- number property ----
	public String snumber = "nan";

	public String getNumber() {
		return this.snumber;
	}

	public void setNumber(String value) {
		this.snumber = value;
	}

	// ---- collection property ----
	public static final String COLLECTION = "collection";

	/**
	 * The Collection of PDBIDs stored in the user session as an array of PDBIDs
	 */
	public PDBID[] getCollection() {
		return (PDBID[]) this.getSession().get(MyActionSupport.COLLECTION);
	}

	@SuppressWarnings("unchecked")
	public void setCollection(PDBID[] values) {
		this.getSession().put(MyActionSupport.COLLECTION, values);
	}

	public static final String COLLECTIONSOURCE = "collectionsource";

	/**
	 * The Collection of PDBIDs stored in the user session as an array of PDBIDs
	 */
	public String getCollectionsource() {
		return (String) this.getSession().get(MyActionSupport.COLLECTIONSOURCE);
	}

	@SuppressWarnings("unchecked")
	public void setCollectionsource(String values) {
		this.getSession().put(MyActionSupport.COLLECTIONSOURCE, values);
	}
}
