package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.List;
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

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentParser {
	private static String		append			= ".done";
	public static FileFilter	commentFilter	= new FileFilter() {
													@Override
													public boolean accept(File pathname) {
														return pathname.isFile() && !pathname.getName().contains(CommentParser.append);
													}
												};

	private Pattern				patternComment	= Pattern.compile("COMMENT: (.+)");
	private Pattern				patternEntry	= Pattern.compile("(.+),(.+)");

	@Autowired
	private AnnotationDAO		anndao;
	@Autowired
	private CommentDAO			comdao;
	@Autowired
	private DatabankDAO			dbdao;
	@Autowired
	private EntryDAO			entdao;

	@Transactional
	public void comment(File file) throws IOException, ParseException {
		//Current comment found or created
		Comment currentComment = new Comment("No comment specified");

		//Databanks & previous databank available as simple caching mechanism
		List<Databank> databanks = dbdao.findAll();
		Databank databank = databanks.get(0);

		//Assign this time to all annotations
		long time = System.currentTimeMillis();

		long start = time, dif;//TODO: Remove

		//Read file
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;
		while ((line = lnr.readLine()) != null) {
			//TODO: Remove
			if (lnr.getLineNumber() % 100 == 0) {
				dif = System.currentTimeMillis() - start;
				System.out.println(lnr.getLineNumber() + ": " + dif / 100.00);
				start = System.currentTimeMillis();
			}

			//Try reading comment line
			if ((matcher = patternComment.matcher(line)).matches()) {
				if (!currentComment.getText().equals(matcher.group(1)))
					currentComment = comdao.findOrCreateByExample(new Comment(matcher.group(1)), "id", "annotations");
			}
			else
				//Try reading entry line
				if ((matcher = patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						//Find databank
						if (databanks.contains(databank = new Databank(matcher.group(1))))
							databank = databanks.get(databanks.indexOf(databank));
						else
							throw new ParseException("No databank found for name " + matcher.group(1) + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find or create entry
					Entry entry = entdao.findOrCreateByExample(new Entry(databank, matcher.group(2).toLowerCase()), "id", "file", "annotations");

					//Create & store annotation
					entry.getAnnotations().add(new Annotation(currentComment, entry, time));
				}
				else
					throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		}
		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
		Logger.getLogger(CommentParser.class).info("Commented file" + file.getAbsolutePath());
	}

	@Transactional
	public void uncomment(File file) throws IOException, ParseException {
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

		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
		Logger.getLogger(CommentParser.class).info("Uncommented file" + file.getAbsolutePath());
	}

	/**
	 * Cleanup unused comments
	 */
	@Transactional
	public void cleanUpComments() {
		for (Comment comment : comdao.findAll()) {
			entdao.enableFilter("withComment", "comment", comment.getText());
			if (entdao.countAll() == 0)
				comdao.makeTransient(comment);
			entdao.disableFilter("withComment");
		}
		Logger.getLogger(Commenter.class).info("Cleaned up unused comments");
	}

	/**
	 * Cleanup unused entries
	 */
	@Transactional
	public void cleanUpEntries() {
		entdao.enableFilter("withoutFile");
		entdao.enableFilter("withoutComment", "comment", "*");
		for (Entry entry : entdao.findAll())
			entdao.makeTransient(entry);
		entdao.disableFilter("withoutFile");
		entdao.disableFilter("withoutComment");
		Logger.getLogger(Commenter.class).info("Cleaned up unused entries");
	}
}
