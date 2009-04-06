package controller;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Database;
import model.EntryFile;

public class FileCrawler {
	private Database	database;
	private FileFilter	entryfilter, directoryfilter;

	public FileCrawler(Database db) {
		database = db;
		entryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().matches(database.getRegex());
			}
		};
		directoryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				//Sometimes entries are directories: Do not crawl these directories
				return !entryfilter.accept(pathname) && pathname.isDirectory();
			}
		};
	}

	/**
	 * Gets all the FileEntries for the supplied database in the given directory
	 * and any subdirectories
	 * @param dirpath
	 * @return list&lt;EntryFile&gt;
	 */
	public Set<EntryFile> getEntries(String dirpath) {
		Set<EntryFile> entries = new HashSet<EntryFile>();
		for (File dir : dirAndAllSubdirs(new File(dirpath)))
			for (File file : dir.listFiles(entryfilter))
				entries.add(new EntryFile(database, extractPDBID(file.getAbsolutePath()), file.getAbsolutePath(), file.lastModified()));
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

	/**
	 * Extracts the PDBID from the filename using using regular expression group matching:
	 * the PDBID should be enclosed in parentheses () and be the only explicitly matching group
	 * @param filename
	 * @return pdbid
	 */
	private String extractPDBID(String path) {
		Pattern p = Pattern.compile(database.getRegex());
		Matcher m = p.matcher(path);
		if (m.matches())
			return m.group(1);
		throw new IllegalArgumentException(database.getRegex() + " " + path);
	}
}
