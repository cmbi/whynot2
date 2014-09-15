package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.AnnotationRepo;
import nl.ru.cmbi.whynot.hibernate.CommentRepo;
import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.util.SpringUtil;

@Service
public class Annotater {
	private static final Logger	log	= LoggerFactory.getLogger(Annotater.class);

	public static void main(final String[] args) throws Exception {

		Annotater commentParser = (Annotater) SpringUtil.getContext().getBean("annotater");

		if(args.length>0) {

			int i=0;
			while(i+2<=args.length) {

				File file = new File(args[i+1]);
				if(args[i].equals("--comment")) {

					if(file.isFile())
						commentParser.comment(Converter.getFile(file));
				}
				else if(args[i].equals("-uncomment"))
					if(file.isFile())
						commentParser.uncomment(Converter.getFile(file));

				i++;
			}
		}
		else {

			log.info("Annotater start.");
			File dirComments = new File("comment/");
			File dirUncomments = new File("uncomment/");

			//Make sure comment directories exist
			if (!dirComments.isDirectory() && !dirComments.mkdir())
				throw new FileNotFoundException(dirComments.getAbsolutePath());
			if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
				throw new FileNotFoundException(dirUncomments.getAbsolutePath());

			//Any files not already done
			FileFilter commentFilter = new FileFilter() {
				@Override
				public boolean accept(final File pathname) {
					return pathname.isFile() && !pathname.getName().contains(Annotater.append);
				}
			};

			//Comment / Uncomment all files in directories
			for (File file : dirComments.listFiles(commentFilter))
				if (file.length() == 0)
					log.error("File {} is empty and should probably be removed: Skipping it for now.. ", file);
				else
					commentParser.comment(Converter.getFile(file));
			for (File file : dirUncomments.listFiles(commentFilter))
				if (file.length() == 0)
					log.error("File {} is empty and should probably be removed: Skipping it for now.. ", file);
				else
					commentParser.uncomment(Converter.getFile(file));

			commentParser.removeUnusedComments();

			log.info("Annotater done.");
		}
	}

	public static final String	append	= ".done";
	@Autowired
	private AnnotationRepo		anndao;
	@Autowired
	private CommentRepo			comdao;
	@Autowired
	private DatabankRepo			dbdao;
	@Autowired
	private EntryRepo			entdao;

	@PersistenceContext
	private EntityManager		entityManager;

	@Transactional
	public File comment(final File file) throws FileNotFoundException {
		log.info("Adding annotations in " + file.getName());
		Comment comment = new Comment("Empty comment");
		Databank databank = new Databank("Empty databank");
		List<Entry> presentParents = new ArrayList<>();

		int added = 0;
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
				}

				//Skip if there's no present parent for missing entry
				Entry parent = new Entry(databank.getParent(), pdbid);
				if (!presentParents.contains(parent)) {
					log.warn("Skipping annotation for " + dbname + "," + pdbid + ": No present parent");
					continue;
				}

				//Create or find Entry
				Entry entry = entdao.findByDatabankAndPdbid(databank, pdbid);
				if(entry==null) {
					entry = new Entry(databank, pdbid);
					databank.getEntries().add(entry);
				}

				//Add annotation
				if (entry.getAnnotations().add(new Annotation(comment, entry, time)))
					added++;
				else
					log.warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation already present");
			}
			else
				if ((m = Converter.patternCOMMENT.matcher(line)).matches()) {
					//Comment stats
					log.info("COMMENT: " + comment.getText() + ": Adding " + added + " annotations");
					added = 0;

					//Find comment
					String text = m.group(1).trim();
					if ((comment = comdao.findByText(text)) == null)
						comment = new Comment(text);
				}
		}
		scn.close();
		log.info("COMMENT: " + comment.getText() + ": Adding " + added + " annotations");
		File dest = new File(file.getAbsolutePath() + Annotater.append);
		if (file.renameTo(dest))
			return dest;
		throw new FileNotFoundException(dest.getPath() + ": Could not rename file");
	}

	@Transactional
	public File uncomment(final File file) throws FileNotFoundException {
		log.info("Removing annotations in " + file.getName());
		Comment comment = new Comment("Empty comment");
		Databank databank = new Databank("Empty databank");
		List<Entry> previouslyAnnotated = new ArrayList<>();

		int removed = 0, index = 0;
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
					previouslyAnnotated = entdao.getAnnotated(databank);
				}

				//Find Entry
				Entry entry = new Entry(databank, pdbid);
				if (0 <= (index = previouslyAnnotated.indexOf(entry)))
					entry = previouslyAnnotated.get(index);
				else {
					log.warn("Skipping annotation for " + entry.toString() + ": Entry not found");
					continue;
				}

				//Find annotation
				Annotation ann = new Annotation(comment, entry, 1L);
				if (!entry.getAnnotations().contains(ann)) {
					log.warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation not found");
					continue;
				}

				//Delete annotation
				comment.getAnnotations().remove(ann);
				entry.getAnnotations().remove(ann);
				anndao.delete(ann);
				removed++;

				//Delete entry if now empty
				if (entry.getAnnotations().isEmpty() && entry.getFile() == null) {
					databank.getEntries().remove(entry);
					entdao.delete(entry);
				}
			}
			else
				if ((m = Converter.patternCOMMENT.matcher(line)).matches()) {
					//Comment stats
					log.info("COMMENT: " + comment.getText() + ": Removing " + removed + " annotations");
					removed = 0;

					//Find comment
					String text = m.group(1).trim();
					comment = comdao.findByText(text);
					if (comment == null) {
						log.warn("Comment \"" + text + "\" not found!");
						comment = new Comment("Unknown comment");
					}

					//Flush & GC
					((Session) entityManager.getDelegate()).flush();
					System.gc();
				}
		}
		scn.close();

		//Comment stats
		log.info("COMMENT: " + comment.getText() + ": Removing " + removed + " annotations");

		File dest = new File(file.getAbsolutePath() + Annotater.append);
		if (file.renameTo(dest))
			return dest;
		throw new FileNotFoundException(dest.getPath() + ": Could not rename file");
	}

	@Transactional
	public void removeUnusedComments() {
		for (Comment comment : comdao.findAll())
			//Delete previous comment if now empty
			if (comment.getAnnotations().isEmpty()) {
				comdao.delete(comment);
				log.info("Removing unused COMMENT: " + comment.getText());
			}
	}

}
