package dao.hibernate;

import model.Author;
import model.Comment;
import model.Entry;
import model.EntryPK;
import model.File;

import org.hibernate.Session;

import dao.implementations.AnnotationHibernateDAO;
import dao.implementations.DatabankHibernateDAO;
import dao.implementations.GenericHibernateDAO;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;
import dao.interfaces.FileDAO;

public class HibernateDAOFactory extends DAOFactory {
	@Override
	public AnnotationDAO getAnnotationDAO() {
		return (AnnotationDAO) instantiateDAO(AnnotationHibernateDAO.class);
	}

	@Override
	public AuthorDAO getAuthorDAO() {
		return (AuthorDAO) instantiateDAO(AuthorHibernateDAO.class);
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

	private Session	session	= null;

	// You could override this if you don't want HibernateUtil for lookup
	@Override
	public Session getSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	// Inline concrete DAO implementations with no business-related data access methods.
	// If we use public static nested classes, we can centralize all of them in one source file.
	public static class AuthorHibernateDAO extends GenericHibernateDAO<Author, String> implements AuthorDAO {}

	public static class CommentHibernateDAO extends GenericHibernateDAO<Comment, String> implements CommentDAO {}

	public static class EntryHibernateDAO extends GenericHibernateDAO<Entry, EntryPK> implements EntryDAO {}

	public static class FileHibernateDAO extends GenericHibernateDAO<File, EntryPK> implements FileDAO {}
}
