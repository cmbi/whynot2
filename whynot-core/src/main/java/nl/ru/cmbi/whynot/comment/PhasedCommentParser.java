package nl.ru.cmbi.whynot.comment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhasedCommentParser {
	public static final String	append			= ".done";
	public static FileFilter	commentFilter	= new FileFilter() {
													@Override
													public boolean accept(File pathname) {
														return pathname.isFile() && !pathname.getName().contains(PhasedCommentParser.append);
													}
												};

	@Autowired
	private AnnotationDAO		anndao;
	@Autowired
	private CommentDAO			comdao;
	@Autowired
	private DatabankDAO			dbdao;
	@Autowired
	private EntryDAO			entdao;

	@Autowired
	private SessionFactory		sf;

	@Transactional
	public void storeComments(File file) throws IOException, ParseException {
		Logger.getLogger(PhasedCommentParser.class).info("Adding comments in " + file.getName());
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;
		Comment comment = new Comment("Empty comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternCOMMENT.matcher(line)).matches())
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
	}

	@Transactional
	public void storeAnnotations(File file) throws IOException, ParseException {
		Logger.getLogger(PhasedCommentParser.class).info("Adding annotations in " + file.getName());
		int added = 0, skipped = 0;
		long time = System.currentTimeMillis();

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		Databank db = new Databank("Empty databank");
		Entry entry;
		Comment comment = new Comment("Empty comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
				//Find DB
				if (!db.getName().equals(matcher.group(1)))
					db = dbdao.findByName(matcher.group(1));

				//Add or Find Entry
				if (!db.getEntries().add(entry = new Entry(db, matcher.group(2).toLowerCase())))
					entry = entdao.findByDatabankAndPdbid(db, matcher.group(2));

				//Add annotation
				if (entry.getAnnotations().add(new Annotation(comment, entry, time)))
					added++;
				else
					skipped++;
			}
			else
				if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
					Logger.getLogger(PhasedCommentParser.class).info("Added " + added + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
					added = 0;
					skipped = 0;

					//Find comment
					comment = comdao.findByText(matcher.group(1));

					//Flush & GC
					sf.getCurrentSession().flush();
					System.gc();
				}
				else
					throw new ParseException("Expected " + Converter.patternCOMMENT + " or " + Converter.patternEntry + "  on line " + lnr.getLineNumber(), lnr.getLineNumber());
		Logger.getLogger(PhasedCommentParser.class).info("Added " + added + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
		lnr.close();
	}

	@Transactional
	@Deprecated
	public void storeAnnotations1(File file) throws IOException, ParseException {
		Logger.getLogger(PhasedCommentParser.class).info("Adding annotations in " + file.getName());
		int added = 0, skipped = 0;
		long time = System.currentTimeMillis();
		for (Databank db : dbdao.findAll()) {

			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			String line;
			Matcher matcher;
			Pattern pattern = Pattern.compile(db.getName() + ",(.+)");

			Entry entry;
			Comment comment = new Comment("Empty comment");
			while ((line = lnr.readLine()) != null)
				if ((matcher = pattern.matcher(line)).matches()) {
					if (!db.getEntries().add(entry = new Entry(db, matcher.group(1).toLowerCase())))
						entry = entdao.findByDatabankAndPdbid(db, matcher.group(1));
					if (entry.getAnnotations().add(new Annotation(comment, entry, time)))
						added++;
					else
						skipped++;
				}
				else
					if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
						Logger.getLogger(PhasedCommentParser.class).info(db.getName() + ": " + added + " added, " + skipped + " skipped for comment: \"" + comment.getText() + "\"");
						comment = comdao.findByText(matcher.group(1));
						added = 0;
						skipped = 0;
					}
			lnr.close();
			sf.getCurrentSession().flush();
			System.gc();
		}
	}

	@Transactional
	@Deprecated
	public void storeAnnotations2(File file) throws IOException, ParseException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		long time = System.currentTimeMillis();
		int added = 0, skipped = 0;
		String line;
		Matcher matcher;
		Comment comment = new Comment("Unspecified comment");
		Databank databank = new Databank("Unspecified databank");
		Entry entry = new Entry(databank, "Unspecified");
		List<Annotation> annotations = new ArrayList<Annotation>();
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
				System.err.println(lnr.getLineNumber());
				if (!comment.getText().equals(matcher.group(1))) {
					comment.getAnnotations().addAll(annotations);
					comment = comdao.findByText(matcher.group(1));
				}
			}
			else
				if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						databank = dbdao.findByName(matcher.group(1));

					entry = entdao.findByDatabankAndPdbid(databank, matcher.group(2));
					annotations.add(new Annotation(comment, entry, time));
				}
				else
					throw new ParseException("Expected: " + Converter.patternCOMMENT.pattern() + " OR " + Converter.patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		comment.getAnnotations().addAll(annotations);

		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + PhasedCommentParser.append));
		Logger.getLogger(PhasedCommentParser.class).info("Added " + added + ", skipped " + skipped + " annotations from file: " + file.getAbsolutePath());
	}

	@Transactional
	public void old_comment(File file) throws IOException, ParseException {
		//Current comment found or created
		Comment comment = new Comment("No comment specified");

		//Databank available as simple caching mechanism
		Databank databank = new Databank("Unknown databank");

		Entry entry;

		//Assign this time to all annotations
		long time = System.currentTimeMillis();

		int added = 0, skipped = 0;

		//Read file
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;
		while ((line = lnr.readLine()) != null)
			//Try reading comment line
			if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
			}
			else
				//Try reading entry line
				if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						if (null == (databank = dbdao.findByName(matcher.group(1))))
							throw new ParseException("No databank found for name " + matcher.group(1) + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find or create entry
					String pdbid = matcher.group(2).toLowerCase();
					if (null == (entry = entdao.findByDatabankAndPdbid(databank, pdbid))) {
						entry = new Entry(databank, pdbid);
						databank.getEntries().add(entry);
					}

					//Create & store annotation
					Annotation ann = new Annotation(comment, entry, time);
					if (entry.getAnnotations().add(ann))
						added++;
					else {
						Logger.getLogger(PhasedCommentParser.class).warn("Annotation found, skipping: " + ann);
						skipped++;
					}
				}
				else
					throw new ParseException("Expected: " + Converter.patternCOMMENT.pattern() + " OR " + Converter.patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + PhasedCommentParser.append));
		Logger.getLogger(PhasedCommentParser.class).info("Added " + added + ", skipped " + skipped + " annotations from file: " + file.getAbsolutePath());
	}

	@Transactional
	public void old_uncomment(File file) throws IOException, ParseException {
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
			if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
			}
			else
				//Try reading entry line
				if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
					if (!databank.getName().equals(matcher.group(1)))
						if (null == (databank = dbdao.findByName(matcher.group(1))))
							throw new ParseException("No databank found for name " + matcher.group(1) + " at line " + lnr.getLineNumber(), lnr.getLineNumber());

					//Find entry
					String pdbid = matcher.group(2).toLowerCase();
					if (null == (entry = entdao.findByDatabankAndPdbid(databank, pdbid))) {
						Logger.getLogger(PhasedCommentParser.class).warn("Entry not found, skipping: " + comment + "," + line);
						skipped++;
						continue;
					}

					//Find annotation
					Annotation ann = new Annotation(comment, entry, 1L);
					if (!entry.getAnnotations().contains(ann)) {
						Logger.getLogger(PhasedCommentParser.class).warn("Annotation not found, skipping: " + ann);
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
					throw new ParseException("Expected: " + Converter.patternCOMMENT.pattern() + " OR " + Converter.patternEntry.pattern() + " at line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();

		file.renameTo(new File(file.getAbsolutePath() + PhasedCommentParser.append));
		Logger.getLogger(PhasedCommentParser.class).info("Removed " + removed + ", skipped " + skipped + " annotations from file: " + file.getAbsolutePath());
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
		Logger.getLogger(PhasedCommentParser.class).info("Cleaned up " + count + " unused comments");
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
		Logger.getLogger(PhasedCommentParser.class).info("Cleaned up " + count + " unused entries");
	}
}
