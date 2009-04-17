package annotate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.AnnotationPK;
import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;
import model.EntryPK;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class Commenter {
	private static DAOFactory			factory			= DAOFactory.instance(DAOFactory.HIBERNATE);

	private static final String			COMMENTDIR		= "comment/";
	private static final String			UNCOMMENTDIR	= "uncomment/";

	private static final FilenameFilter	txtfilter		= new FilenameFilter() {
															public boolean accept(File dir, String name) {
																return name.endsWith(".txt");
															}
														};

	private static final Pattern		patternAuthor	= Pattern.compile("AUTHOR: (.+)");
	private static final Pattern		patternComment	= Pattern.compile("COMMENT: (.+)");
	private static final Pattern		patternEntry	= Pattern.compile("(.+),(.+)");

	public static void main(String[] args) throws Exception {
		if (!new File(Commenter.COMMENTDIR).isDirectory())
			if (!new File(Commenter.COMMENTDIR).mkdir())
				throw new FileNotFoundException(Commenter.COMMENTDIR);
		for (String path : new File(Commenter.COMMENTDIR).list(Commenter.txtfilter))
			Commenter.comment(Commenter.COMMENTDIR + path);

		if (!new File(Commenter.UNCOMMENTDIR).isDirectory())
			if (!new File(Commenter.UNCOMMENTDIR).mkdir())
				throw new FileNotFoundException(Commenter.UNCOMMENTDIR);
		for (String path : new File(Commenter.UNCOMMENTDIR).list(Commenter.txtfilter))
			Commenter.uncomment(Commenter.UNCOMMENTDIR + path);
	}

	public static boolean comment(String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Commenter.factory.getCurrentSession().beginTransaction(); //Plain JDBC

			//Initialize DAO's
			AuthorDAO authdao = Commenter.factory.getAuthorDAO();
			CommentDAO comdao = Commenter.factory.getCommentDAO();
			DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
			EntryDAO entdao = Commenter.factory.getEntryDAO();

			BufferedReader bf = new BufferedReader(new FileReader(path));
			Matcher m;
			String line;
			Author author;
			Comment comment;

			//Get Author
			m = Commenter.patternAuthor.matcher(line = bf.readLine());
			if (m.matches()) {
				author = authdao.findById(m.group(1), true);
				if (author == null)
					author = new Author(m.group(1));
			}
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			//Get Comment
			m = Commenter.patternComment.matcher(line = bf.readLine());
			if (m.matches()) {
				comment = comdao.findById(m.group(1), true);
				if (comment == null)
					comment = new Comment(m.group(1));
			}
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			//Loop Entries
			long thetime = System.currentTimeMillis();
			while ((line = bf.readLine()) != null) {
				m = Commenter.patternEntry.matcher(line);
				if (m.matches()) {
					String db = m.group(1);
					String id = m.group(2).toUpperCase();
					Databank databank = dbdao.findById(db, false);
					Entry entry = entdao.findById(new EntryPK(databank, id), true);
					if (entry == null)
						entry = new Entry(databank, id);
					new Annotation(author, comment, entry, thetime);
				}
				else
					throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);
			}

			transact.commit(); //Plain JDBC

			//Rename file to prevent rerunning
			new File(path).renameTo(new File(path.concat(".done")));

			succes = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Commenter.class).info(path + ": Succes");
			else
				Logger.getLogger(Commenter.class).error(path + ": Failure");
		}
		return succes;
	}

	public static boolean uncomment(String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Commenter.factory.getCurrentSession().beginTransaction(); //Plain JDBC

			//Initialize DAO's
			AnnotationDAO anndao = Commenter.factory.getAnnotationDAO();
			AuthorDAO authdao = Commenter.factory.getAuthorDAO();
			CommentDAO comdao = Commenter.factory.getCommentDAO();
			DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
			EntryDAO entdao = Commenter.factory.getEntryDAO();

			BufferedReader bf = new BufferedReader(new FileReader(path));
			Matcher m;
			String line;
			Author author;
			Comment comment;

			//Get Author
			m = Commenter.patternAuthor.matcher(line = bf.readLine());
			if (m.matches()) {
				author = authdao.findById(m.group(1), true);
				if (author == null)
					throw new IllegalArgumentException("Author not found");
			}
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			//Get Comment
			m = Commenter.patternComment.matcher(line = bf.readLine());
			if (m.matches()) {
				comment = comdao.findById(m.group(1), true);
				if (comment == null)
					throw new IllegalArgumentException("Comment not found");
			}
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			//Loop Entries
			while ((line = bf.readLine()) != null) {
				m = Commenter.patternEntry.matcher(line);
				if (m.matches()) {
					String db = m.group(1);
					String id = m.group(2).toUpperCase();
					Databank databank = dbdao.findById(db, false);
					if (databank == null)
						throw new IllegalArgumentException("Databank '" + db + "' not found");
					Entry entry = entdao.findById(new EntryPK(databank, id), true);
					if (entry == null)
						throw new IllegalArgumentException("Entry '" + db + "/" + id + "' not found");
					Annotation annotation = anndao.findById(new AnnotationPK(comment, entry), true);
					if (annotation != null)
						anndao.makeTransient(annotation);
				}
				else
					throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);
			}

			transact.commit(); //Plain JDBC

			//Rename file to prevent rerunning
			new File(path).renameTo(new File(path.concat(".done")));

			succes = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Commenter.class).info(path + ": Succes");
			else
				Logger.getLogger(Commenter.class).error(path + ": Failure");
		}
		return succes;
	}
}
