package interfaces;

import model.Database;

public interface DatabaseDAO extends GenericDAO<Database, String> {
	long getMissingCount(Database db);
}
