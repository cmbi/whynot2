package nl.ru.cmbi.whynot.jsp.whynot;

import model.PDBID;
import nl.ru.cmbi.whynot.all.MyActionSupport;
import persistance.PersistanceException;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class WhyNot extends MyActionSupport {

	@Override
	public String execute() {
		if (this.hasErrors())
			return Action.ERROR;
		return Action.SUCCESS;
	}

	public String setCollectionToPDBIDsWithComment() {
		int comid = Integer.valueOf(this.getScomid());
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
