package dao.hibernate;

import org.hibernate.Session;

import dao.interfaces.AnnotationDAO;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

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

	public abstract Session getCurrentSession();

	// Add your DAO interfaces here
	public abstract AnnotationDAO getAnnotationDAO();

	public abstract AuthorDAO getAuthorDAO();

	public abstract CommentDAO getCommentDAO();

	public abstract DatabankDAO getDatabankDAO();

	public abstract EntryDAO getEntryDAO();
}
