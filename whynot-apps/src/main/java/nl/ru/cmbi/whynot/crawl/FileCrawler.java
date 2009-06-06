package nl.ru.cmbi.whynot.crawl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.log4j.Logger;

public class FileCrawler extends AbstractCrawler {
	public FileCrawler(Databank databank, FileDAO filedao) {
		super(databank, filedao);
	}

	@Override
	public void addEntriesIn(File path) {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		int crawled = 0, updated = 0, added = 0, index;
		for (File dir : dirAndAllSubdirs(databank, path))
			for (File file : dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pattern.matcher(pathname.getAbsolutePath()).matches();
				}
			})) {
				Matcher m = pattern.matcher(file.getAbsolutePath());
				if (m.matches()) {
					crawled++;

					String id = m.group(1).toLowerCase();

					//Find or create entry
					Entry entry = new Entry(databank, id);
					if (0 <= (index = oldEntries.indexOf(entry))) {
						entry = oldEntries.get(index);
						entry.getAnnotations().clear();
					}
					else
						newEntries.add(entry);

					nl.ru.cmbi.whynot.model.File stored = entry.getFile();
					nl.ru.cmbi.whynot.model.File found = new nl.ru.cmbi.whynot.model.File(file);

					//Create or correct file
					if (stored == null) {
						entry.setFile(found);
						added++;
					}
					else
						if (!stored.equals(found)) {
							filedao.makeTransient(entry.getFile());
							entry.setFile(found);
							updated++;
						}
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
	private SortedSet<File> dirAndAllSubdirs(final Databank databank, File directory) {
		SortedSet<File> directories = new TreeSet<File>();
		directories.add(directory); // Add this
		for (File subdir : directory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				//Sometimes entries are directories: Do not crawl these directories
				return !pattern.matcher(pathname.getAbsolutePath()).matches() && pathname.isDirectory();
			}
		}))
			directories.addAll(dirAndAllSubdirs(databank, subdir)); // Add recursive subdirs
		return directories;
	}
}
