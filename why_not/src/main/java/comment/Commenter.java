package comment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.AuthorDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class Commenter {
	private DAOFactory		factory;

	private FilenameFilter	txtfilter		= new FilenameFilter() {
												public boolean accept(File dir, String name) {
													return name.endsWith(".txt");
												}
											};

	private Pattern			patternAuthor	= Pattern.compile("AUTHOR: (.+)");
	private Pattern			patternComment	= Pattern.compile("COMMENT: (.+)");
	private Pattern			patternEntry	= Pattern.compile("(.+),(.+)");

	public static void main(String[] args) throws Exception {
		Commenter commenter = new Commenter();
		commenter.commentAll();
		commenter.uncommentAll();
	}

	public Commenter() {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	private void commentAll() throws Exception {
		String COMMENTDIR = "comment/";
		for (String path : getDir(COMMENTDIR).list(txtfilter))
			comment(COMMENTDIR + path);
	}

	private void uncommentAll() throws Exception {
		String UNCOMMENTDIR = "uncomment/";
		for (String path : getDir(UNCOMMENTDIR).list(txtfilter))
			uncomment(UNCOMMENTDIR + path);
	}

	public boolean comment(String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		LineNumberReader lnr = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC
			lnr = new LineNumberReader(new FileReader(path));

			Matcher m;
			String line;

			//Get Author
			m = patternAuthor.matcher(line = lnr.readLine());
			if (!m.matches())
				throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);
			Author author = new Author(m.group(1));

			//Get Comment
			m = patternComment.matcher(line = lnr.readLine());
			if (!m.matches())
				throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);
			Comment comment = new Comment(m.group(1));

			//Loop Entries
			DatabankDAO dbdao = factory.getDatabankDAO();
			List<Databank> databanks = dbdao.findAll();

			long thetime = System.currentTimeMillis();
			while ((line = lnr.readLine()) != null) {
				m = patternEntry.matcher(line);
				if (!m.matches())
					throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);
				String db = m.group(1);
				String id = m.group(2).toLowerCase();
				Databank databank = dbdao.findById(db, true);
				Entry entry = new Entry(databank, id);
				new Annotation(author, comment, entry, thetime);
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
			if (lnr != null)
				lnr.close();

			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Commenter.class).info(path + ": Succes");
			else
				Logger.getLogger(Commenter.class).error(path + ": Failure");
		}
		return succes;
	}

	public boolean uncomment(String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		BufferedReader bf = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC

			//Initialize DAO's
			AnnotationDAO anndao = factory.getAnnotationDAO();
			AuthorDAO authdao = factory.getAuthorDAO();
			CommentDAO comdao = factory.getCommentDAO();
			DatabankDAO dbdao = factory.getDatabankDAO();
			EntryDAO entdao = factory.getEntryDAO();

			bf = new BufferedReader(new FileReader(path));
			Matcher m;
			String line;

			//Get Author
			m = patternAuthor.matcher(line = bf.readLine());
			if (!m.matches())
				throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);
			Author author = authdao.findById(m.group(1), true);
			if (author == null)
				throw new IllegalArgumentException("Author not found");

			//Get Comment
			m = patternComment.matcher(line = bf.readLine());
			if (!m.matches())
				throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);
			Comment comment = comdao.findById(m.group(1), true);
			if (comment == null)
				throw new IllegalArgumentException("Comment not found");

			//Loop Entries
			while ((line = bf.readLine()) != null) {
				m = patternEntry.matcher(line);
				if (!m.matches())
					throw new IllegalArgumentException("Expected: " + m.pattern() + ", but got: " + line);

				String db = m.group(1);
				String id = m.group(2).toLowerCase();

				Databank databank = dbdao.findById(db, true);
				if (databank == null)
					throw new IllegalArgumentException("Databank '" + db + "' not found");

				Entry entry = entdao.findById(new EntryPK(databank, id), true);
				if (entry == null)
					throw new IllegalArgumentException("Entry '" + db + "/" + id + "' not found");

				Annotation annotation = anndao.findById(new AnnotationPK(comment, entry), true);
				if (annotation != null)
					anndao.makeTransient(annotation);
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
			if (bf != null)
				bf.close();
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Commenter.class).info(path + ": Succes");
			else
				Logger.getLogger(Commenter.class).error(path + ": Failure");
		}
		return succes;
	}

	private File getDir(String path) throws FileNotFoundException {
		File dir = new File(path);
		if (!dir.isDirectory() && !dir.mkdir())
			throw new FileNotFoundException(path);
		return dir;
	}
}
