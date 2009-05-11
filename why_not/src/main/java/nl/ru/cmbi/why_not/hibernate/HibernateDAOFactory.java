package nl.ru.cmbi.why_not.hibernate;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.why_not.model.Annotation;
import nl.ru.cmbi.why_not.model.Comment;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;
import nl.ru.cmbi.why_not.model.File;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
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
