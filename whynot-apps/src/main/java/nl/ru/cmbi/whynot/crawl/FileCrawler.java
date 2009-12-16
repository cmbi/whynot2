package nl.ru.cmbi.whynot.crawl;

import java.io.FileFilter;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.apache.log4j.Logger;

public class FileCrawler {
	private Databank	databank;
	private EntryDAO	entrydao;
	private Pattern		pattern;

	public FileCrawler(Databank db, EntryDAO entdao) {
		databank = db;
		entrydao = entdao;
		pattern = Pattern.compile(db.getRegex());
	}

	public void crawl(java.io.File crawldir) {
		//Cache old Entries
		List<Entry> annotatedEntries = entrydao.getAnnotated(databank);
		List<Entry> presentEntries = entrydao.getPresent(databank);

		int added = 0;
		Matcher m;

		for (java.io.File dir : dirAndAllSubdirs(crawldir))
			for (java.io.File crawlfile : dir.listFiles()) {
				//Skip files that do not match
				if (!(m = pattern.matcher(crawlfile.getAbsolutePath())).matches())
					continue;
				Entry entry = new Entry(databank, m.group(1).toLowerCase());

				//Skip present entries: No changes at this point if removeChanged ran before
				if (presentEntries.contains(entry))
					continue;

				//Find annotated entry
				int oldEntryIndex = annotatedEntries.indexOf(entry);
				if (0 <= oldEntryIndex) {
					entry = annotatedEntries.get(oldEntryIndex);
					//Delete annotations: We just found it!
					entry.getAnnotations().clear();
				}
				else
					//Add new entry to databank
					if (databank.getEntries().add(entry))
						added++;

				//Set new file
				entry.setFile(new File(crawlfile));
			}

		Logger.getLogger(getClass()).info(databank.getName() + ": Adding " + added + " new Entries");
	}

	/**
	 * Creates set of directories containing argument and all recursive
	 * subdirectories in argument, excluding directories that match entryfilter
	 */
	private SortedSet<java.io.File> dirAndAllSubdirs(java.io.File directory) {
		SortedSet<java.io.File> directories = new TreeSet<java.io.File>();
		directories.add(directory); // Add this
		for (java.io.File subdir : directory.listFiles(new FileFilter() {
			public boolean accept(java.io.File pathname) {
				//Sometimes entries are directories: Do not crawl these directories
				return !pattern.matcher(pathname.getAbsolutePath()).matches() && pathname.isDirectory();
			}
		}))
			directories.addAll(dirAndAllSubdirs(subdir)); // Add recursive subdirs
		return directories;
	}
}
