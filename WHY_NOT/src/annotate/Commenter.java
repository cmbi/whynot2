package annotate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Author;
import model.Comment;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import crawl.Crawler;
import dao.hibernate.DAOFactory;
import dao.hibernate.HibernateUtil;
import dao.interfaces.AnnotationDAO;

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

	public static void main(String[] args) throws Exception {
		if (!new File(Commenter.COMMENTDIR).isDirectory())
			if (!new File(Commenter.COMMENTDIR).mkdir())
				throw new FileNotFoundException(Commenter.COMMENTDIR);
		for (String path : new File(Commenter.COMMENTDIR).list(Commenter.txtfilter))
			Commenter.comment(path);

		if (!new File(Commenter.UNCOMMENTDIR).isDirectory())
			if (!new File(Commenter.UNCOMMENTDIR).mkdir())
				throw new FileNotFoundException(Commenter.UNCOMMENTDIR);
		for (String path : new File(Commenter.UNCOMMENTDIR).list(Commenter.txtfilter))
			Commenter.uncomment(path);
	}

	public static boolean comment(String path) throws Exception {
		BufferedReader bf = new BufferedReader(new FileReader(path));
		Matcher m;
		Author author;
		Comment comment;

		m = Commenter.patternAuthor.matcher(bf.readLine());
		if (m.matches())
			author = new Author(m.group(1));
		else
			throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + m.group());

		m = Commenter.patternComment.matcher(bf.readLine());
		if (m.matches())
			comment = new Comment(m.group(1));
		else
			throw new IllegalArgumentException("Expected: " + m.pattern().pattern() + ", but got: " + m.group());

		for (String line = ""; (line = bf.readLine()) != null;) {
			System.out.println(line);

			;
		}
		new File(path).renameTo(new File(path + "~"));

		return true;
	}

	private static void dostuff() {
		HibernateUtil.getSessionFactory().getCurrentSession();
		AnnotationDAO dbdao = Commenter.factory.getAnnotationDAO();
		Transaction transact = null;
		try {
			transact = Commenter.factory.getCurrentSession().beginTransaction(); //Plain JDBC

			transact.commit(); //Plain JDBC

			Logger.getLogger(Crawler.class).info("Succes");
		}
		catch (RuntimeException e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(Crawler.class).error("Failure");
			throw e;
		}
		finally {
			//Close session if using anything other than current session
		}
	}

	private static void uncomment(String path) {
	// TODO Auto-generated method stub

	}
}
