package comment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import dao.hibernate.DAOFactory;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class LegacyCommenter {//TODO Implement
	private DAOFactory	factory;

	private FileFilter	commentFilter	= new FileFilter() {
											@Override
											public boolean accept(File pathname) {
												return pathname.isFile() && !pathname.getName().contains(append);
											}
										};
	private String		append			= ".done";

	private Pattern		patternComment	= Pattern.compile("COMMENT: (.+)");
	private Pattern		patternEntry	= Pattern.compile("(.+),(.+)");

	public static void main(String[] args) throws Exception {
		new LegacyCommenter();
	}

	public LegacyCommenter() throws Exception {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);

		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Comment / Uncomment all files in directories
		for (File file : dirComments.listFiles(commentFilter))
			comment(file);
		for (File file : dirUncomments.listFiles(commentFilter))
			uncomment(file);

		//Cleanup unused Comments
		cleanup();
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
				throw new ParseException("Expected: " + patternComment.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

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
							throw new ParseException("No databank with name " + name + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

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
					comment.getAnnotations().add(ann);
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
						throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
			lnr.close();

			transact.commit();

			//Rename file to prevent rerunning
			file.renameTo(new File(file.getAbsolutePath() + append));

			Logger.getLogger(LegacyCommenter.class).info("Completed file" + file.getAbsolutePath());
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(LegacyCommenter.class).info("Failed on file" + file.getAbsolutePath());
			throw e;
		}
	}

	public void uncomment(File file) throws Exception {
		Transaction transact = null;
		LineNumberReader lnr = null;
		try {
			transact = factory.getSession().beginTransaction(); //Plain JDBC
			AnnotationDAO anndao = factory.getAnnotationDAO();
			CommentDAO comdao = factory.getCommentDAO();
			DatabankDAO dbdao = factory.getDatabankDAO();
			EntryDAO entdao = factory.getEntryDAO();

			//Read first line (starting comment)
			lnr = new LineNumberReader(new FileReader(file));
			String line = lnr.readLine();
			Matcher matcher = patternComment.matcher(line);
			if (!matcher.matches())
				throw new ParseException("Expected: " + patternComment.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

			//Find comment
			Comment comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)));
			if (comment == null)
				throw new ParseException("No comment with text " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

			//Previous databank available as simple caching mechanism
			Databank databank = null;

			//Read rest of file
			while ((line = lnr.readLine()) != null)
				if ((matcher = patternEntry.matcher(line)).matches()) {
					//Find databank
					String name = matcher.group(1);
					if (databank == null || !databank.getName().equals(name))
						if ((databank = dbdao.findByNaturalId(Restrictions.naturalId().set("name", name))) == null)
							throw new ParseException("No databank with name " + name + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find entry
					String pdbid = matcher.group(2).toLowerCase();
					Entry entry = entdao.findByNaturalId(Restrictions.naturalId().set("databank", databank).set("pdbid", pdbid));
					if (entry == null)
						continue;

					//Create & store annotation
					Annotation ann = anndao.findByNaturalId(Restrictions.naturalId().set("comment", comment).set("entry", entry));
					if (ann != null) {
						comment.getAnnotations().remove(ann);
						entry.getAnnotations().remove(ann);
						anndao.makeTransient(ann);
					}
				}
				else
					//Maybe its a new comment
					if ((matcher = patternComment.matcher(line)).matches()) {
						//Find comment
						comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)));
						if (comment == null)
							throw new ParseException("No comment with text " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
					}
					else
						throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
			lnr.close();

			transact.commit();

			//Rename file to prevent rerunning
			file.renameTo(new File(file.getAbsolutePath() + append));

			Logger.getLogger(LegacyCommenter.class).info("Completed file" + file.getAbsolutePath());
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(LegacyCommenter.class).info("Failed on file" + file.getAbsolutePath());
			throw e;
		}
	}
}
