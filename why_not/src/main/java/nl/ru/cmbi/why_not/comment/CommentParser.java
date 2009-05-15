package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentParser {
	public static final String	append			= ".done";
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
		Comment comment = new Comment("No comment specified");

		//Databank available as simple caching mechanism
		Databank databank = new Databank("Unknown databank");

		Entry entry;

		//Assign this time to all annotations
		long time = System.currentTimeMillis();

		int added = 0, skipped = 0;

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
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
			}
			else
				//Try reading entry line
				if ((matcher = patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						if (null == (databank = dbdao.findByName(matcher.group(1))))
							throw new ParseException("No databank found for name " + matcher.group(1) + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find or create entry
					String pdbid = matcher.group(2).toLowerCase();
					if (null == (entry = entdao.findByDatabankAndPdbid(databank, pdbid))) {
						entry = new Entry(databank, pdbid.toLowerCase());
						databank.getEntries().add(entry);
					}

					//Create & store annotation
					Annotation ann = new Annotation(comment, entry, time);
					if (entry.getAnnotations().add(ann))
						added++;
					else {
						Logger.getLogger(CommentParser.class).warn("Annotation found, skipping: " + ann);
						skipped++;
					}
				}
				else
					throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		}
		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
		Logger.getLogger(CommentParser.class).info("Added " + added + ", skipped " + skipped + " annotations from file: " + file.getAbsolutePath());
	}

	@Transactional
	public void uncomment(File file) throws IOException, ParseException {
		//Current comment found or created
		Comment comment = new Comment("No comment specified");

		//Databank available as simple caching mechanism
		Databank databank = new Databank("Unknown databank");

		Entry entry;

		int removed = 0, skipped = 0;

		//Read file
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;
		while ((line = lnr.readLine()) != null)
			//Try reading comment line
			if ((matcher = patternComment.matcher(line)).matches()) {
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
			}
			else
				//Try reading entry line
				if ((matcher = patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						if (null == (databank = dbdao.findByName(matcher.group(1))))
							throw new ParseException("No databank found for name " + matcher.group(1) + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find entry
					String pdbid = matcher.group(2).toLowerCase();
					if (null == (entry = entdao.findByDatabankAndPdbid(databank, pdbid))) {
						Logger.getLogger(CommentParser.class).warn("Entry not found, skipping: " + comment + "," + line);
						skipped++;
						continue;
					}

					//Find annotation
					Annotation ann = new Annotation(comment, entry, 1L);
					if (!entry.getAnnotations().contains(ann)) {
						Logger.getLogger(CommentParser.class).warn("Annotation not found, skipping: " + ann);
						skipped++;
						continue;
					}

					//Remove annotation
					comment.getAnnotations().remove(ann);
					entry.getAnnotations().remove(ann);
					anndao.makeTransient(ann);
					removed++;
				}
				else
					throw new ParseException("Expected: " + patternComment.pattern() + " OR " + patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
		Logger.getLogger(CommentParser.class).info("Removed " + removed + ", skipped " + skipped + " annotations from file: " + file.getAbsolutePath());
	}

	/**
	 * Cleanup unused comments
	 */
	@Transactional
	public void cleanUpComments() {
		int count = 0;
		for (Comment comment : comdao.findAll()) {
			entdao.enableFilter("withComment", "comment", comment.getText());
			if (entdao.countAll() == 0) {
				comdao.makeTransient(comment);
				count++;
			}
			entdao.disableFilter("withComment");
		}
		Logger.getLogger(CommentParser.class).info("Cleaned up " + count + " unused comments");
	}

	/**
	 * Cleanup unused entries
	 */
	@Transactional
	public void cleanUpEntries() {
		int count = 0;
		entdao.enableFilter("withoutFile");
		entdao.enableFilter("withoutComment", "comment", "%");
		for (Entry entry : entdao.findAll()) {
			entdao.makeTransient(entry);
			count++;
		}
		entdao.disableFilter("withoutFile");
		entdao.disableFilter("withoutComment");
		Logger.getLogger(CommentParser.class).info("Cleaned up " + count + " unused entries");
	}
}
