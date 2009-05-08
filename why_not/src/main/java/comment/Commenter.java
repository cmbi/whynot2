package comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import dao.hibernate.DAOFactory;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class Commenter {
	private DAOFactory				factory;

	private static FilenameFilter	commentFilter	= new FilenameFilter() {
														public boolean accept(File dir, String name) {
															return !name.contains(append);
														}
													};
	private static String			append			= ".done";

	private Pattern					patternComment	= Pattern.compile("COMMENT: (.+)");
	private Pattern					patternEntry	= Pattern.compile("(.+),(.+)");

	public static void main(String[] args) throws Exception {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Comment / Uncomment all files in directories
		Commenter commenter = new Commenter();
		for (File file : dirComments.listFiles(commentFilter))
			commenter.comment(file);
		for (File file : dirUncomments.listFiles(commentFilter))
			;//commenter.uncomment(file);
	}

	public Commenter() {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	public void comment(File file) throws Exception {
		Transaction transact = null;
		LineNumberReader lnr = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC
			CommentDAO comdao = factory.getCommentDAO();
			DatabankDAO dbdao = factory.getDatabankDAO();
			EntryDAO entdao = factory.getEntryDAO();

			//Read first line (starting comment)
			lnr = new LineNumberReader(new FileReader(file));
			String line = lnr.readLine();
			Matcher matcher = patternComment.matcher(line);
			if (!matcher.matches())
				throw new ParseException("Expected: " + patternComment.pattern(), lnr.getLineNumber());

			//Find / create comment
			Comment comment = new Comment(matcher.group(1));
			Comment strdCom = comdao.findByNaturalId(Restrictions.naturalId().set("text", comment.getText()));
			if (strdCom != null)
				comment = strdCom;

			//Previous databank available as simple caching mechanism
			Databank databank = null;

			//Assign this time to all annotations
			long time = System.currentTimeMillis();

			//Read rest of file
			while ((line = lnr.readLine()) != null)
				if ((matcher = patternEntry.matcher(line)).matches()) {
					//Find databank
					String name = matcher.group(1);
					if (databank == null || !databank.getName().equals(name))
						if ((databank = dbdao.findByNaturalId(Restrictions.naturalId().set("name", name))) == null)
							throw new ParseException("No databank with name " + name + " found.", lnr.getLineNumber());

					//Find / create entry
					String pdbid = matcher.group(2).toLowerCase();
					Entry entry = new Entry(databank, pdbid);
					Entry strdEnt = entdao.findByNaturalId(Restrictions.naturalId().set("databank", entry.getDatabank()).set("pdbid", entry.getPdbid()));
					if (strdEnt != null)
						entry = strdEnt;
					else
						entdao.makePersistent(entry);

					//Create & store annotation
					Annotation ann = new Annotation(comment, entry, time);
					entry.getAnnotations().add(ann);
				}
				else
					//Maybe its a new comment
					if ((matcher = patternComment.matcher(line)).matches()) {
						//Find / create comment
						comment = new Comment(matcher.group(1));
						strdCom = comdao.findByNaturalId(Restrictions.naturalId().set("text", comment.getText()));
						if (strdCom != null)
							comment = strdCom;
					}
					else
						throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern(), lnr.getLineNumber());
			lnr.close();

			transact.commit();

			//Rename file to prevent rerunning
			file.renameTo(new File(file.getAbsolutePath() + append));
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			throw e;
		}
	}
}
