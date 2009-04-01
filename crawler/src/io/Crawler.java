package io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crawler {
	private FileFilter	entryfilter, directoryfilter;

	public Crawler(final String regex) {
		entryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().matches(regex);
			}
		};
		directoryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				//Sometimes a directory is an entry: Do not crawl this directory
				return !entryfilter.accept(pathname) && pathname.isDirectory();
			}
		};
	}

	public List<File> getEntries(String dirpath) {
		List<File> entries = new ArrayList<File>();
		for (File dir : dirAndAllSubdirs(new File(dirpath)))
			entries.addAll(Arrays.asList(dir.listFiles(entryfilter)));
		return entries;
	}

	/**
	 * Creates list of directories containing argument and all recursive
	 * subdirectories in argument, excluding directories that match entryfilter
	 */
	private List<File> dirAndAllSubdirs(File directory) {
		List<File> directories = new ArrayList<File>();
		directories.add(directory); // Add this
		for (File subdir : directory.listFiles(directoryfilter))
			directories.addAll(dirAndAllSubdirs(subdir)); // Add recursive subdirs
		return directories;
	}
}
