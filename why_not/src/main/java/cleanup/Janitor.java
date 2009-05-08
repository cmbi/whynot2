package cleanup;

import model.Comment;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.CommentDAO;

public class Janitor {
	public static void main(String[] args) throws Exception {
		new Janitor().cleanup();
	}

	private void cleanup() throws Exception {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		Transaction transact = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC

			Janitor.cleanComments(factory);

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

	public static void cleanComments(DAOFactory factory) {
		CommentDAO comdao = factory.getCommentDAO();
		for (Comment entity : comdao.findAll())
			if (entity.getAnnotations().isEmpty()) {
				comdao.makeTransient(entity);
				Logger.getLogger(Janitor.class).info("Removed: " + entity);
			}
	}
}
