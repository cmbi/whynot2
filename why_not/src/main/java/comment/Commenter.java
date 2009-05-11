package comment;

import hibernate.DAOFactory;
import hibernate.GenericDAO.CommentDAO;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import model.Comment;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;


public class Commenter {
	private static String		append			= ".done";
	private static FileFilter	commentFilter	= new FileFilter() {
													@Override
													public boolean accept(File pathname) {
														return pathname.isFile() && !pathname.getName().contains(append);
													}
												};

	public static void main(String[] args) throws Exception {
		Commenter commenter = new Commenter();

		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Comment / Uncomment all files in directories
		for (File file : dirComments.listFiles(commentFilter))
			commenter.comment(file);
		for (File file : dirUncomments.listFiles(commentFilter))
			commenter.uncomment(file);

		//Cleanup unused Comments
		commenter.cleanup();
	}

	protected static DAOFactory	factory;
	private Transaction			transact;

	public Commenter() throws Exception {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	public void comment(File file) throws Exception {
		try {
			transact = Commenter.factory.getSession().beginTransaction(); //Plain JDBC
			{
				String line = readFirstLine(file);
				if (line.startsWith("PDBID"))
					new LegacyCommenter().comment(file);
				else
					if (line.startsWith("COMMENT"))
						new NewCommenter().comment(file);

				//Rename file to prevent rerunning
				file.renameTo(new File(file.getAbsolutePath() + Commenter.append));
			}
			transact.commit();
			Logger.getLogger(NewCommenter.class).info("Completed file" + file.getAbsolutePath());
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(NewCommenter.class).info("Failed on file" + file.getAbsolutePath());
			throw e;
		}
	}

	public void uncomment(File file) throws Exception {
		try {
			transact = Commenter.factory.getSession().beginTransaction(); //Plain JDBC
			{
				String line = readFirstLine(file);
				if (line.startsWith("PDBID"))
					new LegacyCommenter().uncomment(file);
				else
					if (line.startsWith("COMMENT"))
						new NewCommenter().uncomment(file);

				//Rename file to prevent rerunning
				file.renameTo(new File(file.getAbsolutePath() + Commenter.append));
			}
			transact.commit();
			Logger.getLogger(NewCommenter.class).info("Completed file" + file.getAbsolutePath());
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(NewCommenter.class).info("Failed on file" + file.getAbsolutePath());
			throw e;
		}
	}

	private String readFirstLine(File file) throws FileNotFoundException, IOException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line = lnr.readLine();
		lnr.close();
		return line;
	}

	public void cleanup() throws Exception {
		Transaction transact = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC
			CommentDAO comdao = factory.getCommentDAO();
			for (Comment comment : comdao.findAll())
				if (comment.getAnnotations().isEmpty())
					comdao.makeTransient(comment);
			transact.commit();
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			throw e;
		}
	}
}
