package cleanup;

import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class Janitor {
	public static void main(String[] args) throws Exception {
		new Janitor().cleanup();
	}

	private void cleanup() throws Exception {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		Transaction transact = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC

			//AnnotationDAO anndao = factory.getAnnotationDAO();

			Janitor.cleanAuthors(factory);

			Janitor.cleanComments(factory);

			Janitor.cleanEntries(factory);

			Janitor.cleanDatabases(factory);

			//FileDAO filedao = factory.getFileDAO();

			transact.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			if (transact != null)
				transact.rollback();
			throw e;
		}
		finally {

		}
	}

	public static void cleanAuthors(DAOFactory factory) {
		AuthorDAO authdao = factory.getAuthorDAO();
		for (Author entity : authdao.findAll())
			if (entity.getAnnotations().isEmpty()) {
				authdao.makeTransient(entity);
				Logger.getLogger(Janitor.class).info("Removed: " + entity);
			}
	}

	public static void cleanComments(DAOFactory factory) {
		CommentDAO comdao = factory.getCommentDAO();
		for (Comment entity : comdao.findAll())
			if (entity.getAnnotations().isEmpty()) {
				comdao.makeTransient(entity);
				Logger.getLogger(Janitor.class).info("Removed: " + entity);
			}
	}

	public static void cleanDatabases(DAOFactory factory) {
		DatabankDAO dbdao = factory.getDatabankDAO();
		for (Databank entity : dbdao.findAll())
			if (entity.getEntries().isEmpty() && entity.getFiles().isEmpty()) {
				dbdao.makeTransient(entity);
				Logger.getLogger(Janitor.class).info("Removed: " + entity);
			}
	}

	public static void cleanEntries(DAOFactory factory) {
		EntryDAO entdao = factory.getEntryDAO();
		for (Entry entity : entdao.findAll())
			if (entity.getAnnotations().isEmpty()) {
				entity.getDatabank().getEntries().remove(entity);
				Logger.getLogger(Janitor.class).info("Removed: " + entity);
			}
	}

}
