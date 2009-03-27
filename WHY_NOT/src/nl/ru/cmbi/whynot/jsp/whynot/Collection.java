package nl.ru.cmbi.whynot.jsp.whynot;

import persistance.PersistanceException;
import model.Database;
import nl.ru.cmbi.whynot.all.MyActionSupport;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class Collection extends MyActionSupport {
	public static final String DETAIL = "detail";

	private int levelOfDetail = 0;

	@Override
	public String execute() {
		Database[] databases = null;
		try { databases = this.getRDB().getDatabases(); }
		catch (PersistanceException e) {
			e.printStackTrace();
		}
		if (databases == null)
			this.addActionError(this.getText("rdb.error.databases"));
		this.setDatabases(databases);

		if (this.hasErrors())
			return Action.ERROR;
		return Action.SUCCESS;
	}

	//#####  #####

	public String getLevelOfDetail(){
		return ""+levelOfDetail;
	}

	public String setLevelOfDetail(){
		this.levelOfDetail = Integer.parseInt(this.getSdetail());
		return execute();
	}
}
