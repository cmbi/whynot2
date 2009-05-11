package hibernate;

import hibernate.GenericDAO.AnnotationDAO;
import hibernate.GenericDAO.CommentDAO;
import hibernate.GenericDAO.DatabankDAO;
import hibernate.GenericDAO.EntryDAO;
import hibernate.GenericDAO.FileDAO;
import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;
import model.File;

import org.hibernate.Session;


public class HibernateDAOFactory extends DAOFactory {
	@Override
	public AnnotationDAO getAnnotationDAO() {
		return (AnnotationDAO) instantiateDAO(AnnotationHibernateDAO.class);
	}

	@Override
	public CommentDAO getCommentDAO() {
		return (CommentDAO) instantiateDAO(CommentHibernateDAO.class);
	}

	@Override
	public DatabankDAO getDatabankDAO() {
		return (DatabankDAO) instantiateDAO(DatabankHibernateDAO.class);
	}

	@Override
	public EntryDAO getEntryDAO() {
		return (EntryDAO) instantiateDAO(EntryHibernateDAO.class);
	}

	@Override
	public FileDAO getFileDAO() {
		return (FileDAO) instantiateDAO(FileHibernateDAO.class);
	}

	@SuppressWarnings("unchecked")
	private GenericHibernateDAO instantiateDAO(Class daoClass) {
		try {
			GenericHibernateDAO dao = (GenericHibernateDAO) daoClass.newInstance();
			dao.setSession(getSession());
			return dao;
		}
		catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}

	// You could override this if you don't want HibernateUtil for lookup
	@Override
	public Session getSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	// Inline concrete DAO implementations with no business-related data access methods.
	// If we use public static nested classes, we can centralize all of them in one source file.
	public static class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Long> implements AnnotationDAO {
	}

	public static class CommentHibernateDAO extends GenericHibernateDAO<Comment, Long> implements CommentDAO {
	}

	public static class DatabankHibernateDAO extends GenericHibernateDAO<Databank, Long> implements DatabankDAO {
	}

	public static class EntryHibernateDAO extends GenericHibernateDAO<Entry, Long> implements EntryDAO {
	}

	public static class FileHibernateDAO extends GenericHibernateDAO<File, Long> implements FileDAO {
	}
}
