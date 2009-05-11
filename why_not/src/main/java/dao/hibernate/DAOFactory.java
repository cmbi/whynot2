package dao.hibernate;

import org.hibernate.Session;

import dao.interfaces.GenericDAO.AnnotationDAO;
import dao.interfaces.GenericDAO.CommentDAO;
import dao.interfaces.GenericDAO.DatabankDAO;
import dao.interfaces.GenericDAO.EntryDAO;
import dao.interfaces.GenericDAO.FileDAO;

public abstract class DAOFactory {

	/**
	 * Creates a standalone DAOFactory that returns unmanaged DAO
	 * beans for use in any environment Hibernate has been configured
	 * for. Uses HibernateUtil/SessionFactory and Hibernate context
	 * propagation (CurrentSessionContext), thread-bound or transaction-bound,
	 * and transaction scoped.
	 */
	@SuppressWarnings("unchecked")
	public static final Class	HIBERNATE	= HibernateDAOFactory.class;

	/**
	 * Factory method for instantiation of concrete factories.
	 */
	@SuppressWarnings("unchecked")
	public static DAOFactory instance(Class factory) {
		try {
			return (DAOFactory) factory.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException("Couldn't create DAOFactory: " + factory);
		}
	}

	public abstract Session getSession();

	// Add your DAO interfaces here
	public abstract AnnotationDAO getAnnotationDAO();

	public abstract CommentDAO getCommentDAO();

	public abstract DatabankDAO getDatabankDAO();

	public abstract EntryDAO getEntryDAO();

	public abstract FileDAO getFileDAO();
}
