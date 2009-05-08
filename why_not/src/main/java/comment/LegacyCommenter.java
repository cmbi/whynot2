package comment;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;

import org.hibernate.criterion.Restrictions;

import dao.interfaces.AnnotationDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class LegacyCommenter {//TODO Implement
	private Pattern	patternPDBID	= Pattern.compile("PDBID        : (.+)");
	private Pattern	patternDatabase	= Pattern.compile("Database     : (.+)");
	private Pattern	patternProperty	= Pattern.compile("Property     : (.+)");
	private Pattern	patternComment	= Pattern.compile("Comment      : (.+)");

	public void comment(File file) throws Exception {
		CommentDAO comdao = Commenter.factory.getCommentDAO();
		DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
		EntryDAO entdao = Commenter.factory.getEntryDAO();

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		String pdbid = null;
		Databank databank = null;
		Comment comment = null;

		//Assign this time to all annotations
		long time = System.currentTimeMillis();

		//Read file
		while ((line = lnr.readLine()) != null)
			//Read first line (starting pdbid)
			if ((matcher = patternPDBID.matcher(line)).matches())
				pdbid = matcher.group(1);
			else
				if ((matcher = patternDatabase.matcher(line)).matches()) {
					//Find databank
					if (databank == null || !databank.getName().equals(matcher.group(1)))
						if ((databank = dbdao.findByNaturalId(Restrictions.naturalId().set("name", matcher.group(1)))) == null)
							throw new ParseException("No databank with name " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
				}
				else
					if ((matcher = patternComment.matcher(line)).matches()) {
						//Find / create comment
						if (comment == null || !comment.getText().equals(matcher.group(1)))
							if ((comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)))) == null)
								comment = new Comment(matcher.group(1));
					}
					else
						if ((matcher = patternProperty.matcher(line)).matches())
							;//We scrapped Properties
						else
							if (line.equals("//")) {
								//Find / create entry
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
								throw new ParseException("Expected: " + patternPDBID.pattern() + " OR " + patternDatabase.pattern() + " OR " + patternProperty.pattern() + " OR " + patternComment.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
	}

	public void uncomment(File file) throws Exception {
		AnnotationDAO anndao = Commenter.factory.getAnnotationDAO();
		CommentDAO comdao = Commenter.factory.getCommentDAO();
		DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
		EntryDAO entdao = Commenter.factory.getEntryDAO();

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		String pdbid = null;
		Databank databank = null;
		Comment comment = null;

		//Read file
		while ((line = lnr.readLine()) != null)
			//Read first line (starting pdbid)
			if ((matcher = patternPDBID.matcher(line)).matches())
				pdbid = matcher.group(1);
			else
				if ((matcher = patternDatabase.matcher(line)).matches()) {
					//Find databank
					if (databank == null || !databank.getName().equals(matcher.group(1)))
						if ((databank = dbdao.findByNaturalId(Restrictions.naturalId().set("name", matcher.group(1)))) == null)
							throw new ParseException("No databank with name " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
				}
				else
					if ((matcher = patternComment.matcher(line)).matches()) {
						//Find / create comment
						if (comment == null || !comment.getText().equals(matcher.group(1)))
							if ((comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)))) == null)
								throw new ParseException("No comment with text " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
					}
					else
						if ((matcher = patternProperty.matcher(line)).matches())
							;//We scrapped Properties
						else
							if (line.equals("//")) {
								//Find entry
								Entry entry = entdao.findByNaturalId(Restrictions.naturalId().set("databank", databank).set("pdbid", pdbid));
								if (entry == null)
									continue;

								//Find & remove annotation
								Annotation ann = anndao.findByNaturalId(Restrictions.naturalId().set("comment", comment).set("entry", entry));
								if (ann != null) {
									comment.getAnnotations().remove(ann);
									entry.getAnnotations().remove(ann);
									anndao.makeTransient(ann);
								}
							}
							else
								throw new ParseException("Expected: " + patternPDBID.pattern() + " OR " + patternDatabase.pattern() + " OR " + patternProperty.pattern() + " OR " + patternComment.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
	}
}
