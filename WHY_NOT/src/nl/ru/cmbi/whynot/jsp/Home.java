package nl.ru.cmbi.whynot.jsp;

import org.apache.struts2.ServletActionContext;

import persistance.PersistanceException;
import persistance.functions.RDBStats;
import nl.ru.cmbi.whynot.all.MyActionSupport;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class Home extends MyActionSupport {
	public static final String DETAIL = "detail";
	public static final String RDBSTATS = "rdbstats";

	private int levelOfDetail = 0;

	@Override
	public String execute() {
		if (this.hasErrors())
			return Action.ERROR;
		return Action.SUCCESS;
	}

	//#####  #####

	public String getRelationalDatabaseStats() {
		try {
			RDBStats rdbstats = this.getRDB().getRDBStats();
			ServletActionContext.getRequest().setAttribute(Home.RDBSTATS,rdbstats);
		}
		catch (PersistanceException e) {
			e.printStackTrace();
		}
		return "done";
	}

	public String getLevelOfDetail(){
		return ""+levelOfDetail;
	}

	public String setLevelOfDetail(){
		this.levelOfDetail = Integer.parseInt(this.getSdetail());
		return Action.SUCCESS;
	}
}
