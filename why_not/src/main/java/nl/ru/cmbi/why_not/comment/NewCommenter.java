package nl.ru.cmbi.why_not.comment;


import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.model.Annotation;
import nl.ru.cmbi.why_not.model.Comment;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;

import org.hibernate.criterion.Restrictions;


public class NewCommenter {
	private Pattern	patternComment	= Pattern.compile("COMMENT: (.+)");
	private Pattern	patternEntry	= Pattern.compile("(.+),(.+)");

	public void comment(File file) throws Exception {
		CommentDAO comdao = Commenter.factory.getCommentDAO();
		DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
		EntryDAO entdao = Commenter.factory.getEntryDAO();

		//Read first line (starting comment)
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line = lnr.readLine();
		Matcher matcher = patternComment.matcher(line);
		if (!matcher.matches())
			throw new ParseException("Expected: " + patternComment.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

		//Find / create comment
		Comment comment;
		if ((comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)))) == null)
			comment = new Comment(matcher.group(1));

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
					if (comment == null || !comment.getText().equals(matcher.group(1)))
						if ((comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)))) == null)
							comment = new Comment(matcher.group(1));
				}
				else
					throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
	}

	public void uncomment(File file) throws Exception {
		AnnotationDAO anndao = Commenter.factory.getAnnotationDAO();
		CommentDAO comdao = Commenter.factory.getCommentDAO();
		DatabankDAO dbdao = Commenter.factory.getDatabankDAO();
		EntryDAO entdao = Commenter.factory.getEntryDAO();

		//Read first line (starting comment)
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
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

				//Find & remove annotation
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
					if (comment == null || !comment.getText().equals(matcher.group(1)))
						if ((comment = comdao.findByNaturalId(Restrictions.naturalId().set("text", matcher.group(1)))) == null)
							throw new ParseException("No comment with text " + matcher.group(1) + " found" + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
				}
				else
					throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
	}
}
