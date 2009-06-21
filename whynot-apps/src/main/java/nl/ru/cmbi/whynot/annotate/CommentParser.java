package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

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
public class CommentParser {
	public static final String	append	= ".done";
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
	public File comment(File file) throws FileNotFoundException {
		Logger.getLogger(getClass()).info("Adding annotations in " + file.getName());
		Comment comment = new Comment("Empty comment");
		Databank databank = new Databank("Empty databank");
		List<Entry> presentParents = new ArrayList<Entry>();
		List<Entry> previouslyAnnotated = new ArrayList<Entry>();

		int added = 0, index = 0;
		long time = System.currentTimeMillis();
		Matcher m;
		Scanner scn = new Scanner(file);
		while (scn.hasNextLine()) {
			String line = scn.nextLine();
			if ((m = Converter.patternEntry.matcher(line)).matches()) {
				String dbname = m.group(1);
				String pdbid = m.group(2).toLowerCase();
				//Check if databank still the same as current
				if (!databank.getName().equals(dbname)) {
					databank = dbdao.findByName(dbname);
					presentParents = entdao.getMissing(databank);
					previouslyAnnotated = entdao.getAnnotated(databank);
				}

				//Skip if there's no present parent for missing entry
				if (!presentParents.contains(new Entry(databank.getParent(), pdbid))) {
					Logger.getLogger(getClass()).warn("Skipping annotation for " + dbname + "," + pdbid + ": No missing parent");
					continue;
				}

				//Create or find Entry
				Entry entry = new Entry(databank, pdbid);
				if (0 <= (index = previouslyAnnotated.indexOf(entry)))
					entry = previouslyAnnotated.get(index);
				else
					databank.getEntries().add(entry);

				//Add annotation
				if (entry.getAnnotations().add(new Annotation(comment, entry, time)))
					added++;
				else
					Logger.getLogger(getClass()).warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation already present");
			}
			else
				if ((m = Converter.patternCOMMENT.matcher(line)).matches()) {
					//Comment stats
					Logger.getLogger(getClass()).info("COMMENT: " + comment.getText() + ": Adding " + added + " annotations");
					added = 0;

					//Find comment
					String text = m.group(1).trim();
					if ((comment = comdao.findByText(text)) == null)
						comment = new Comment(text);
				}
		}
		scn.close();
		Logger.getLogger(getClass()).info("COMMENT: " + comment.getText() + ": Adding " + added + " annotations");
		File dest = new File(file.getAbsolutePath() + CommentParser.append);
		file.renameTo(dest);
		return dest;
	}

	@Transactional
	public File comment_old(File file) throws IOException, ParseException {
		Logger.getLogger(getClass()).info("Adding annotations in " + file.getName());
		int added = 0, skipped = 0;
		long time = System.currentTimeMillis();

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line;
		Matcher matcher;

		Databank db = new Databank("Empty databank");
		//List<Entry> entries = new ArrayList<Entry>(db.getEntries());
		Entry entry;
		Comment comment = new Comment("Empty comment");
		while ((line = lnr.readLine()) != null)
			if ((matcher = Converter.patternEntry.matcher(line)).matches()) {
				//Find DB
				if (!db.getName().equals(matcher.group(1))) {
					db = dbdao.findByName(matcher.group(1));
					;//entries = new ArrayList<Entry>(db.getEntries());
				}

				//Add or Find Entry
				if (!db.getEntries().add(entry = new Entry(db, matcher.group(2).toLowerCase())))
					//entry = entries.get(entries.indexOf(entry));
					entry = entdao.findByDatabankAndPdbid(db, matcher.group(2));

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
					comment = comdao.findByText(matcher.group(1).trim());
					if (comment == null)
						comment = new Comment(matcher.group(1).trim());

					//Flush & GC
					sf.getCurrentSession().flush();
					System.gc();
				}
				else
					throw new ParseException("Expected " + Converter.patternCOMMENT + " or " + Converter.patternEntry + "  on line " + lnr.getLineNumber(), lnr.getLineNumber());
		lnr.close();
		Logger.getLogger(getClass()).info("Added " + added + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
		File dest = new File(file.getAbsolutePath() + CommentParser.append);
		file.renameTo(dest);
		return dest;
	}

	@Transactional
	public File uncomment(File file) throws IOException, ParseException {
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

				//Delete annotation
				comment.getAnnotations().remove(ann);
				entry.getAnnotations().remove(ann);
				anndao.makeTransient(ann);
				removed++;

				//Delete entry if empty
				if (entry.getAnnotations().isEmpty() && entry.getFile() == null) {
					db.getEntries().remove(entry);
					entdao.makeTransient(entry);
				}
			}
			else
				if ((matcher = Converter.patternCOMMENT.matcher(line)).matches()) {
					Logger.getLogger(getClass()).info("Removed " + removed + ", skipped " + skipped + " for comment: \"" + comment.getText() + "\"");
					//Delete previous comment if now empty
					if (comment.getAnnotations().isEmpty()) {
						comdao.makeTransient(comment);
						Logger.getLogger(getClass()).info("Removed unused comment: " + comment);
					}
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
		File dest = new File(file.getAbsolutePath() + CommentParser.append);
		file.renameTo(dest);
		return dest;
	}

	@Transactional
	public int removeEntriesWithoutBothFileAndParentFile() {
		return entdao.removeEntriesWithoutBothFileAndParentFile();
	}
}
