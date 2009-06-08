package nl.ru.cmbi.whynot.crawl;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.apache.log4j.Logger;

public class FileCrawler {
	protected Databank	databank;
	protected FileDAO	filedao;
	protected Pattern	pattern;

	public FileCrawler(Databank db, FileDAO fldao) {
		databank = db;
		filedao = fldao;
		pattern = Pattern.compile(db.getRegex());
	}

	public void crawl(java.io.File path) {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		int crawled = 0, updated = 0, added = 0, index;
		for (java.io.File dir : dirAndAllSubdirs(databank, path))
			for (java.io.File file : dir.listFiles(new FileFilter() {
				public boolean accept(java.io.File pathname) {
					return pattern.matcher(pathname.getAbsolutePath()).matches();
				}
			})) {
				Matcher m = pattern.matcher(file.getAbsolutePath());
				if (!m.matches())
					continue;
				crawled++;
				String id = m.group(1).toLowerCase();

				//Find or create entry
				Entry entry = new Entry(databank, id);
				if (0 <= (index = oldEntries.indexOf(entry)))
					entry = oldEntries.get(index);
				else
					newEntries.add(entry);

				//Find or create file
				File stored = entry.getFile();
				File found = new File(file);
				if (found.equals(stored))
					continue;//We're done

				//Set new file
				entry.setFile(found);
				//Delete annotations: We just found it!
				entry.getAnnotations().clear();

				if (stored == null)
					added++;
				else {
					//Delete old file
					filedao.makeTransient(stored);
					updated++;
				}

			}
		//Add new entries to databank
		databank.getEntries().addAll(newEntries);

		Logger.getLogger(getClass()).info(databank.getName() + ": Crawled " + crawled + ", Updated " + updated + ", Added " + added);
	}

	/**
	 * Creates set of directories containing argument and all recursive
	 * subdirectories in argument, excluding directories that match entryfilter
	 */
	private SortedSet<java.io.File> dirAndAllSubdirs(final Databank databank, java.io.File directory) {
		SortedSet<java.io.File> directories = new TreeSet<java.io.File>();
		directories.add(directory); // Add this
		for (java.io.File subdir : directory.listFiles(new FileFilter() {
			public boolean accept(java.io.File pathname) {
				//Sometimes entries are directories: Do not crawl these directories
				return !pattern.matcher(pathname.getAbsolutePath()).matches() && pathname.isDirectory();
			}
		}))
			directories.addAll(dirAndAllSubdirs(databank, subdir)); // Add recursive subdirs
		return directories;
	}
}
