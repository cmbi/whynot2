package dao.hibernate;

import model.Annotation;
import model.Author;
import model.Comment;

import org.hibernate.Session;

import dao.implementations.DatabaseHibernateDAO;
import dao.implementations.GenericHibernateDAO;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabaseDAO;

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
	public DatabaseDAO getDatabaseDAO() {
		return (DatabaseDAO) instantiateDAO(DatabaseHibernateDAO.class);
	}

	@SuppressWarnings("unchecked")
	private GenericHibernateDAO instantiateDAO(Class daoClass) {
		try {
			GenericHibernateDAO dao = (GenericHibernateDAO) daoClass.newInstance();
			dao.setSession(getCurrentSession());//TODO Session really here?
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
	public static class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Integer> implements AnnotationDAO {}

	public static class AuthorHibernateDAO extends GenericHibernateDAO<Author, String> implements AuthorDAO {}

	public static class CommentHibernateDAO extends GenericHibernateDAO<Comment, String> implements CommentDAO {}
}
