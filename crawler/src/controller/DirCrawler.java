package controller;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import model.Database;
import model.EntryFile;

public class DirCrawler extends AbstractCrawler {
	private FileFilter	entryfilter, directoryfilter;

	/**
	 * Recursive directory crawler
	 * @param db
	 */
	public DirCrawler(Database db) {
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
	public Set<EntryFile> getEntries(String dirpath) {
		Set<EntryFile> entries = new HashSet<EntryFile>();
		for (File dir : dirAndAllSubdirs(new File(dirpath)))
			for (File file : dir.listFiles(entryfilter)) {
				Matcher m = pattern.matcher(file.getAbsolutePath());
				if (m.matches())
					entries.add(new EntryFile(database, m.group(1), file.getAbsolutePath(), file.lastModified()));
				else
					throw new IllegalArgumentException(database.getRegex() + " & " + file.getAbsolutePath());
			}
		return entries;
	}

	/**
	 * Creates set of directories containing argument and all recursive
	 * subdirectories in argument, excluding directories that match entryfilter
	 */
	private Set<File> dirAndAllSubdirs(File directory) {
		Set<File> directories = new HashSet<File>();
		directories.add(directory); // Add this
		for (File subdir : directory.listFiles(directoryfilter))
			directories.addAll(dirAndAllSubdirs(subdir)); // Add recursive subdirs
		return directories;
	}
}
