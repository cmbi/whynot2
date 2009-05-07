package crawl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;

import dao.interfaces.FileDAO;

public class FileCrawler extends AbstractCrawler {
	private FileFilter	entryfilter, directoryfilter;

	/**
	 * Recursive directory crawler
	 * @param db
	 */
	public FileCrawler(Databank db) {
		super(db);
		entryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pattern.matcher(pathname.getAbsolutePath()).matches();
			}
		};
		directoryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				//Sometimes entries are directories: Do not crawl these directories
				return !entryfilter.accept(pathname) && pathname.isDirectory();
			}
		};
	}

	@Override
	public void addEntriesIn(String path) {
		FileDAO fldao = Crawler.factory.getFileDAO();

		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		int crawled = 0, updated = 0, added = 0, index;
		for (File dir : dirAndAllSubdirs(new File(path)))
			for (File file : dir.listFiles(entryfilter)) {
				Matcher m = pattern.matcher(file.getAbsolutePath());
				if (m.matches()) {
					crawled++;

					String id = m.group(1).toLowerCase();

					//Find or create entry
					Entry entry = new Entry(databank, id);
					if (0 <= (index = oldEntries.indexOf(entry)))
						entry = oldEntries.get(index);
					else
						newEntries.add(entry);

					model.File stored = entry.getFile();
					model.File found = new model.File(file);

					//Create or correct file
					if (stored == null) {
						entry.setFile(found);
						added++;
					}
					else
						if (!stored.equals(found)) {
							fldao.makeTransient(entry.getFile());
							entry.setFile(found);
							updated++;
						}
				}
			}
		databank.getEntries().addAll(newEntries);

		Logger.getLogger(FileCrawler.class).info(databank.getName() + ": Crawled " + crawled + ", Updated " + updated + ", Added " + added);
	}

	/**
	 * Creates set of directories containing argument and all recursive
	 * subdirectories in argument, excluding directories that match entryfilter
	 */
	private SortedSet<File> dirAndAllSubdirs(File directory) {
		SortedSet<File> directories = new TreeSet<File>();
		directories.add(directory); // Add this
		for (File subdir : directory.listFiles(directoryfilter))
			directories.addAll(dirAndAllSubdirs(subdir)); // Add recursive subdirs
		return directories;
	}
}
