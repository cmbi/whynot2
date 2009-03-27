package nl.ru.cmbi.whynot.jsp.whynot;

import model.Comment;
import model.PDBID;
import nl.ru.cmbi.whynot.all.MyActionSupport;

import org.apache.struts2.ServletActionContext;

import persistance.PersistanceException;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class Comments extends MyActionSupport {
	public static final String WITHCOMMENT = "withcomment";

	@Override
	public String execute() {
		Comment[] comments = null;
		try { comments = this.getRDB().getComments(); }
		catch (PersistanceException e) {}
		if (comments == null)
			this.addActionError(this.getText("rdb.error.comments"));
		this.setComments(comments);

		if (this.hasErrors())
			return Action.ERROR;
		return Action.SUCCESS;
	}

	public String getCommentStats() {
		int scomid = Integer.parseInt(this.getScomid());
		try {
			PDBID[] pdbids = this.getRDB().getPDBIDsWithComment(scomid);
			ServletActionContext.getRequest().setAttribute(Comments.WITHCOMMENT,""+pdbids.length);
		}
		catch (PersistanceException e) {}
		return "done";
	}

	//#####  #####

	public String withcomment(){
		int scomid = Integer.parseInt(this.getScomid());
		return this.setCollectionToPDBIDsWithComment(scomid);
	}

	//######  ######

	public String setCollectionToPDBIDsWithComment(int comid) {
		String comment = this.getRDB().getComment(comid).getComment();
		PDBID[] pdbids = null;
		try {pdbids = this.getRDB().getPDBIDsWithComment(comid);}
		catch (PersistanceException e){}
		if (pdbids == null)
			this.addActionError(this.getText("rdb.error.functions"));
		this.setCollection(pdbids);
		this.setCollectionsource("All PDBIDs with comment \"" + comment + "\".");
		return "collection";
	}
}
