package nl.ru.cmbi.whynot.jsp.about;

import model.Database;
import nl.ru.cmbi.whynot.all.MyActionSupport;
import persistance.PersistanceException;

import com.opensymphony.xwork2.Action;

/**
 * <p> Validate a user login. </p>
 */
@SuppressWarnings("serial")
public final class Databases extends MyActionSupport {
	@Override
	public String execute() {
		Database[] databases = null;
		try {
			databases = this.getRDB().getDatabases();
		}
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
}
