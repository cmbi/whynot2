package nl.ru.cmbi.whynot.jsp;

import model.Database;
import model.PDBID;
import nl.ru.cmbi.whynot.all.MyActionSupport;

import org.apache.struts2.ServletActionContext;

import persistance.PersistanceException;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
public final class Form extends MyActionSupport {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String ENTRY = "entry";
	public static final String COMMENTS = "comments";

	public String execute() {
		String spdbid = this.getSpdbid();
		PDBID pdbid = this.getRDB().getPDBID(spdbid);
		if (pdbid == null)
			this.addActionError(this.getText("form.error.execution.spdbid_not_found"));
		this.setPDBID(pdbid);

		Database[] databases = null;
		try { databases = this.getRDB().getDatabases(); }
		catch (PersistanceException e) {
			e.printStackTrace();
		}
		if (databases == null)
			this.addActionError(this.getText("rdb.error.databases"));
		this.setDatabases(databases);

		if (this.hasErrors())
			return Action.INPUT;
		return Action.SUCCESS;
	}

	public String setEntry() throws Exception {
		String spdbid = this.getSpdbid();
		String sdatabase = this.getSdatabase();
		ServletActionContext.getRequest().setAttribute(Form.ENTRY,this.getRDB().getEntry(spdbid, sdatabase));
		return "done";
	}

	public String setComments() throws Exception {
		String spdbid = this.getSpdbid();
		String sdatabase = this.getSdatabase();
		String sproperty = this.getSproperty();
		ServletActionContext.getRequest().setAttribute(Form.COMMENTS,this.getRDB().getCommentsForPropertyOfEntry(spdbid, sdatabase, sproperty));
		return "done";
	}
}
