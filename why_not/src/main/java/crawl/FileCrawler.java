package crawl;

import java.io.File;
import java.io.FileFilter;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import model.Databank;
import model.Entry;

import org.hibernate.criterion.Restrictions;

import dao.interfaces.EntryDAO;

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
	public int addEntriesIn(String path) {
		EntryDAO entdao = Crawler.factory.getEntryDAO();

		int count = 0;
		for (File dir : dirAndAllSubdirs(new File(path)))
			for (File file : dir.listFiles(entryfilter)) {
				Matcher m = pattern.matcher(file.getAbsolutePath());
				if (m.matches()) {
					String id = m.group(1).toLowerCase();

					//Find or create entry
					Entry entry = entdao.findByNaturalId(Restrictions.naturalId().set("databank", database).set("pdbid", id));
					if (entry == null)
						entry = new Entry(database, id);

					model.File stored = entry.getFile();
					model.File found = new model.File(file);

					//Create or correct file
					if (stored == null) {
						entry.setFile(found);
						count++;
					}
					else
						if (!stored.equals(found)) {
							stored.setPath(found.getPath());
							stored.setTimestamp(found.getTimestamp());
						}
				}
				if (count % 100 == 1)
					System.out.println(count);
			}
		return count;
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
