package nl.ru.cmbi.why_not.hibernate;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.FileDAO;

import org.hibernate.Session;

public interface DAOFactory {
	public Session getSession();

	// Add your DAO interfaces here
	public AnnotationDAO getAnnotationDAO();

	public CommentDAO getCommentDAO();

	public DatabankDAO getDatabankDAO();

	public EntryDAO getEntryDAO();

	public FileDAO getFileDAO();
}
