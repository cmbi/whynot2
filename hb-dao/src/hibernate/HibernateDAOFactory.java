package hibernate;

import hello.HibernateUtil;
import interfaces.DatabaseDAO;
import model.Database;

import org.hibernate.Session;

public class HibernateDAOFactory extends DAOFactory {
	@Override
	public DatabaseDAO getDatabaseDAO() {
		return (DatabaseDAO) instantiateDAO(DatabaseDAOHibernate.class);
	}

	private GenericHibernateDAO instantiateDAO(Class daoClass) {
		try {
			GenericHibernateDAO dao = (GenericHibernateDAO) daoClass.newInstance();
			dao.setSession(getCurrentSession());
			return dao;
		}
		catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}

	// You could override this if you don't want HibernateUtil for lookup
	protected Session getCurrentSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	// Inline concrete DAO implementations with no business-related data access methods.
	// If we use public static nested classes, we can centralize all of them in one source file.
	public static class DatabaseDAOHibernate extends GenericHibernateDAO<Database, String> implements DatabaseDAO {}
}
