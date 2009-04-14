package hibernate;

import interfaces.DatabaseDAO;
import interfaces.GenericDAO;
import model.Annotation;
import model.Author;
import model.Comment;
import model.Database;

import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateDAOFactory extends DAOFactory {
	@Override
	public DatabaseDAO getDatabaseDAO() {
		return (DatabaseDAO) instantiateDAO(DatabaseHibernateDAO.class);
	}

	@SuppressWarnings("unchecked")
	private GenericHibernateDAO instantiateDAO(Class daoClass) {
		try {
			GenericHibernateDAO dao = (GenericHibernateDAO) daoClass.newInstance();
			dao.setSession(getCurrentSession());//TODO
			return dao;
		}
		catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}

	// You could override this if you don't want HibernateUtil for lookup
	@Override
	public Session getCurrentSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	// Inline concrete DAO implementations with no business-related data access methods.
	// If we use public static nested classes, we can centralize all of them in one source file.
	public static class AuthorHibernateDAO extends GenericHibernateDAO<Author, String> implements GenericDAO<Author, String> {}

	public static class CommentHibernateDAO extends GenericHibernateDAO<Comment, String> implements GenericDAO<Comment, String> {}

	public static class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Integer> implements GenericDAO<Annotation, Integer> {}

	public static class DatabaseHibernateDAO extends GenericHibernateDAO<Database, String> implements DatabaseDAO {
		private static final String	VALID		= //
												"from EntryFile par, EntryFile chi " + //
												"where chi.entry.database = :child and " + //
												"par.entry = ( chi.entry.database.parent, chi.entry.pdbid )";

		private static final String	MISSING		= // 
												"from EntryFile par " + //
												"where par.entry.database = :parent and " + //
												"(select chi.path from EntryFile chi " + //
												"where chi.entry.database = :child and chi.entry.pdbid = par.entry.pdbid ) is null";

		private static final String	OBSOLETE	= //
												"from EntryFile chi " + //
												"where chi.entry.database = :child and " + //
												"(select par.path from EntryFile par " + //
												"where par.entry = ( chi.entry.database.parent, chi.entry.pdbid )) is null";

		private static final String	ANNOTATED	= // + ( ... )!
												"select ann from Annotation ann" + //
												"where ann.entry IN ";

		private static final String	UNANNOTATED	= // + ( ... )!
												"select ann from Annotation ann" + //
												"where ann.entry NOT IN ";

		public long getMissingCount(Database db) {
			Query q = getSession().createQuery("select count(*) as count " + DatabaseHibernateDAO.MISSING);
			q.setParameter("child", db);
			q.setParameter("parent", db.getParent());
			return (Long) q.uniqueResult();
		}
	}
}
