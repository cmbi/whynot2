package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
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
public class CommentParser {
	public static final String	append			= ".done";
	public static FileFilter	commentFilter	= new FileFilter() {
													@Override
													public boolean accept(File pathname) {
														return pathname.isFile() && !pathname.getName().contains(CommentParser.append);
													}
												};

	@Autowired
	private AnnotationDAO		anndao;
	@Autowired
	private CommentDAO			comdao;
	@Autowired
	private DatabankDAO			dbdao;

	@Autowired
	private SessionFactory		sf;

	@Transactional
	public void comment(File file) throws IOException, ParseException {
		storeComments(file);
		storeAnnotations(file);
	}

	@Transactional
	public void uncomment(File file) throws IOException, ParseException {
		removeAnnotations(file);
	}

	public void storeComments(File file) throws IOException, ParseException {
		Logger.getLogger(getClass()).info("Adding comments in " + file.getName());
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;
		Comment comment = new Comment("Empty comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternCOMMENT.matcher(line)).matches())
				if (!comment.getText().equals(matcher.group(1)))
					comment = comdao.findOrCreateByExample(new Comment(matcher.group(1)));
	}

	public void storeAnnotations(File file) throws IOException, ParseException {
		Logger.getLogger(getClass()).info("Adding annotations in " + file.getName());
		int added = 0, skipped = 0;
		long time = System.currentTimeMillis();

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		Databank db = new Databank("Empty databank");
		List<Entry> entries = new ArrayList<Entry>(db.getEntries());
		Entry entry;
		Comment comment = new Comment("Empty comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
				//Find DB
				if (!db.getName().equals(matcher.group(1))) {
					db = dbdao.findByName(matcher.group(1));
					entries = new ArrayList<Entry>(db.getEntries());
				}

				//Add or Find Entry
				if (!db.getEntries().add(entry = new Entry(db, matcher.group(2).toLowerCase())))
					entry = entries.get(entries.indexOf(entry));
				//	entry = entdao.findByDatabankAndPdbid(db, matcher.group(2));

				//Only annotate missing files
				if (entry.getFile() == null) {
					//Add annotation 
					if (entry.getAnnotations().add(new Annotation(comment, entry, time)))
						added++;
				}
				else
					skipped++;
			}
			else
				if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
					Logger.getLogger(getClass()).info("Added " + added + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
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
		lnr.close();
		Logger.getLogger(getClass()).info("Added " + added + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
	}

	public void removeAnnotations(File file) throws IOException, ParseException {
		Logger.getLogger(getClass()).info("Removing annotations in " + file.getName());
		int removed = 0, skipped = 0;

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		Databank db = new Databank("Empty databank");
		List<Entry> entries = new ArrayList<Entry>(db.getEntries());
		Entry entry;
		Comment comment = new Comment("Unknown comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
				//Find DB
				if (!db.getName().equals(matcher.group(1))) {
					db = dbdao.findByName(matcher.group(1));
					entries = new ArrayList<Entry>(db.getEntries());
				}

				//Find Entry
				if (!entries.contains(entry = new Entry(db, matcher.group(2).toLowerCase()))) {
					Logger.getLogger(getClass()).warn("Entry not found, skipping: " + comment + "," + line);
					skipped++;
					continue;
				}
				entry = entries.get(entries.indexOf(entry));

				//Find annotation
				Annotation ann = new Annotation(comment, entry, 1L);
				if (!entry.getAnnotations().contains(ann)) {
					Logger.getLogger(getClass()).warn("Annotation not found, skipping: " + ann);
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
				if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
					Logger.getLogger(getClass()).info("Removed " + removed + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
					removed = 0;
					skipped = 0;

					//Find comment
					comment = comdao.findByText(matcher.group(1));
					if (comment == null) {
						Logger.getLogger(getClass()).warn("Comment \"" + matcher.group(1) + "\" not found!");
						comment = new Comment("Unknown comment");
					}

					//Flush & GC
					sf.getCurrentSession().flush();
					System.gc();
				}
				else
					throw new ParseException("Expected " + Converter.patternCOMMENT + " or " + Converter.patternEntry + "  on line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
		Logger.getLogger(getClass()).info("Removed " + removed + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
		file.renameTo(new File(file.getAbsolutePath() + CommentParser.append));
	}
}
