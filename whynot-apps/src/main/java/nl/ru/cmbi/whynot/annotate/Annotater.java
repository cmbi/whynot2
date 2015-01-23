package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.WhynotApplication;
import nl.ru.cmbi.whynot.hibernate.AnnotationRepo;
import nl.ru.cmbi.whynot.hibernate.AnnotationRepoImpl;
import nl.ru.cmbi.whynot.hibernate.CommentRepo;
import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

@Service
@Slf4j
public class Annotater {
	public static void main(final String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(WhynotApplication.class);
		Annotater commentParser = applicationContext.getBean(Annotater.class);

		if (args.length > 0) {

			int i = 0;
			while (i + 2 <= args.length) {

				File file = new File(args[i + 1]);
				if (args[i].equals("--comment")) {

					if (file.isFile())
						Annotater.comment(commentParser, Converter.getFile(file));
				}
				else if (args[i].equals("--uncomment")) {

					if (file.isFile())
						commentParser.uncomment(Converter.getFile(file));
				}
				else if(args[i].equals("--replace")) {

					if (file.isDirectory()) {
						Annotater.replaceCommentsFrom( commentParser, file );
					}
				}

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
					Annotater.comment(commentParser, Converter.getFile(file));
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
	private DatabankRepo		dbdao;
	@Autowired
	private EntryRepo			entdao;

	@PersistenceContext
	private EntityManager		entityManager;

	public static void replaceCommentsFrom( final Annotater annotator, final File directory ) throws IOException {

		Map<String,List<String[]>> entries = new HashMap<String,List<String[]>>();

		for( File file : filesInSubPaths( directory ) ) {

			if( file.getName().endsWith(".whynot") ) {

				Map<String,List<String[]>> e = parseWhynotFile ( file );

				for(String comment : e.keySet() ) {

					if( entries.containsKey( comment ) )
						entries.get( comment ) .addAll( e.get( comment ) );
					else
						entries.put( comment ,e.get( comment ) );
				}
			}
		}

		for(String comment : entries.keySet() )
			annotator.replace(comment, entries.get( comment ));
	}

	public static List<File> filesInSubPaths( final File dir ) {

		List<File> files = new ArrayList<File>() ;
		for(File sub : dir.listFiles() ) {

			if( sub.isFile() )
				files.add( sub ) ;
			else if( sub.isDirectory() )
				files.addAll( filesInSubPaths( sub ) );
		}

		return files ;
	}

	public static Map<String,List<String[]>> parseWhynotFile ( final File file ) throws IOException {

		Map<String,List<String[]>> entries = new HashMap<String,List<String[]>>() ;
		String comment=null, line;
		Matcher m;

		log.info("Parsing annotations in " + file.getName());
		try (Scanner scn = new Scanner(file)) {

			while (scn.hasNextLine()) {

				// Determine what type of line we're dealing with
				line = scn.nextLine();
				if ((m = Converter.patternCOMMENT.matcher(line)).matches()) {

					// Store the new Comment
					comment = m.group(1);

					if(! entries.containsKey( comment ) )
						entries.put(comment,new ArrayList<String[]>());
				}
				else {
					if ((m = Converter.patternEntry.matcher(line)).matches())
						// Store the properties associated with entry

						entries.get(comment).add(new String[] { m.group(1), m.group(2).toLowerCase() });
					else
						throw new IllegalStateException("Unrecognized line: " + line);
				}
			}
		}

		return entries;
	}

	public static File comment(final Annotater annotator, final File file) throws IOException {
		log.info("Adding annotations in " + file.getName());
		try (Scanner scn = new Scanner(file)) {
			String comment = null;
			List<String[]> entries = new ArrayList<String[]>();
			Matcher m;
			String line;
			while (scn.hasNextLine()) {

				// Determine what type of line we're dealing with
				line = scn.nextLine();
				if ((m = Converter.patternCOMMENT.matcher(line)).matches()) {
					if (comment != null)
						// Store previous comment and all the entries associated with it
						annotator.comment(comment, entries);

					// Store the new Comment
					comment = m.group(1);
					entries.clear();
				}
				else
					if ((m = Converter.patternEntry.matcher(line)).matches())
						// Store the properties associated with entry
						entries.add(new String[] { m.group(1), m.group(2).toLowerCase() });
					else
						throw new IllegalStateException("Unrecognized line: " + line);
			}

			// Store the final comment and all the entries associated with it
			annotator.comment(comment, entries);
		}

		// Rename file to signal we're done
		File dest = new File(file.getAbsolutePath() + Annotater.append);
		if (file.renameTo(dest))
			return dest;
		throw new FileNotFoundException(dest.getPath() + ": Could not rename file");
	}

	@Transactional
	public void replace(final String commentText, final List<String[]> lines) {

		Long time = System.currentTimeMillis();
		log.info("COMMENT: " + commentText + ": Adding (up to) " + lines.size() + " annotations");

		// Store comment
		Comment comment = comdao.findByText(commentText);
		if (comment == null)
			comment = comdao.save(new Comment(commentText));

		List<Annotation> remove = new ArrayList<Annotation>(), add = new ArrayList<Annotation>() ;

		Databank databank = new Databank("Empty databank");
		for (String[] line : lines) {

			String dbname = line[0],
				pdbid = line[1];

			if (!databank.getName().equals(dbname))
				databank = dbdao.findByName(dbname);

			// Skip if there's no present parent for missing entry
			Entry parent = entdao.findByDatabankAndPdbid(databank.getParent(), pdbid);
			if (parent == null || parent.getFile() == null) {
				log.warn("Skipping annotation for " + dbname + "," + pdbid + ": No present parent");
				continue;
			}

			// Create or find Entry
			Entry entry = entdao.findByDatabankAndPdbid(databank, pdbid);
			if (entry == null) {

				entry = entdao.save(new Entry(databank, pdbid));
			}

			// Replace all annotations by a single one
			boolean present = false;
			for(Annotation a : entry.getAnnotations() ) {

				if( a.getComment().equals( comment ) ) {

					//log.warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation already present");
					present = true;
				}
				else {
					log.debug(pdbid + " : replacing \"{}\" by \"{}\"", a.getComment().getText(), comment.getText());

					remove.add( a );
				}
			}

			if( !present ) {

				add.add( new Annotation(comment, entry, time) ) ;
			}
		}

		for(Annotation a : add) {

			log.debug("save "+a.toString());
			anndao.save( a );
		}

		// Delete annotations on this entry
		for(Annotation a : remove ) {

			a.getEntry().getAnnotations().remove( a );
			a.getComment().getAnnotations().remove( a );

			anndao.delete( a );
		}

		// Add all annotations
		log.info("COMMENT: {}: Added {} annotations", commentText, add.size());
		log.info("removed {} annotations in replacement",remove.size());
	}

	@Transactional
	public void comment(final String commentText, final List<String[]> lines) {
		Long time = System.currentTimeMillis();
		log.info("COMMENT: " + commentText + ": Adding (up to) " + lines.size() + " annotations");

		// Store comment
		Comment comment = comdao.findByText(commentText);
		if (comment == null)
			comment = comdao.save(new Comment(commentText));

		// Store entries for comment
		Databank databank = new Databank("Empty databank");
		List<Annotation> annotations = new ArrayList<>();
		for (String[] line : lines) {
			String dbname = line[0];
			String pdbid = line[1];

			// Check if databank still the same as current
			if (!databank.getName().equals(dbname))
				databank = dbdao.findByName(dbname);

			// Skip if there's no present parent for missing entry
			Entry parent = entdao.findByDatabankAndPdbid(databank.getParent(), pdbid);
			if (parent == null || parent.getFile() == null) {
				log.warn("Skipping annotation for " + dbname + "," + pdbid + ": No present parent");
				continue;
			}

			// Create or find Entry
			Entry entry = entdao.findByDatabankAndPdbid(databank, pdbid);
			if (entry == null)
				entry = entdao.save(new Entry(databank, pdbid));

			// Check which annotations to skip
			if (anndao.findByCommentAndEntry(comment, entry) == null)
				annotations.add(new Annotation(comment, entry, time));
			else
				log.warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation already present");
		}

		// Add all annotations
		anndao.save(annotations);
		log.info("COMMENT: {}: Added {} annotations", commentText, annotations.size());
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
			log.debug("uncomment line "+line );
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
				Annotation ann = anndao.findByCommentAndEntry(comment, entry);
				if (ann == null ) {
				//if (!entry.getAnnotations().contains(ann)) {
					log.warn("Skipping annotation for " + dbname + "," + pdbid + ": Annotation not found");
					continue;
				}

				log.debug("delete "+ann.toString());

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
