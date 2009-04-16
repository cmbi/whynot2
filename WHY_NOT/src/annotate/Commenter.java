package annotate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.hibernate.HibernateUtil;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.DatabankDAO;

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
		HibernateUtil.getSessionFactory().getCurrentSession();
		AnnotationDAO anndao = Commenter.factory.getAnnotationDAO();
		DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
		Transaction transact = null;
		try {
			transact = Commenter.factory.getCurrentSession().beginTransaction(); //Plain JDBC
			BufferedReader bf = new BufferedReader(new FileReader(path));
			Matcher m;
			String line;
			Author author;
			Comment comment;

			m = Commenter.patternAuthor.matcher(line = bf.readLine());
			if (m.matches())
				author = new Author(m.group(1));
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			m = Commenter.patternComment.matcher(line = bf.readLine());
			if (m.matches())
				comment = new Comment(m.group(1));
			else
				throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);

			Annotation ann = new Annotation(author, comment);

			while ((line = bf.readLine()) != null) {
				m = Commenter.patternEntry.matcher(line);
				if (m.matches()) {
					String db = m.group(1);
					String id = m.group(2);
					Databank databank = dbdao.findById(db, false);
					//ann.getEntries().add(new Entry(databank, id));
				}
				else
					throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + line);
			}
			new File(path).renameTo(new File(path + "~"));

			anndao.makePersistent(ann);
			transact.commit(); //Plain JDBC

			succes = true;
		}
		catch (RuntimeException e) {
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Commenter.class).info("Succes");
			else
				Logger.getLogger(Commenter.class).error("Failure");
		}
		return succes;
	}

	private static void uncomment(String path) {
	// TODO Auto-generated method stub

	}
}
