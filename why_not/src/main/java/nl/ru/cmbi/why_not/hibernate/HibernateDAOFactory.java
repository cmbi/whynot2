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

import org.springframework.stereotype.Service;

public class HibernateDAOFactory {
	// Inline concrete DAO implementations with no business-related data access methods.
	// If we use public static nested classes, we can centralize all of them in one source file.
	@Service
	public static class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Long> implements AnnotationDAO {
	}

	@Service
	public static class CommentHibernateDAO extends GenericHibernateDAO<Comment, Long> implements CommentDAO {
	}

	@Service
	public static class DatabankHibernateDAO extends GenericHibernateDAO<Databank, Long> implements DatabankDAO {
	}

	@Service
	public static class EntryHibernateDAO extends GenericHibernateDAO<Entry, Long> implements EntryDAO {
	}

	@Service
	public static class FileHibernateDAO extends GenericHibernateDAO<File, Long> implements FileDAO {
	}
}
