package dao.interfaces;

import dao.hibernate.GenericDAO;
import model.Database;

public interface DatabaseDAO extends GenericDAO<Database, String> {
	long getMissingCount(Database db);

}
