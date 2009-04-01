package io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DirectoryCrawler implements java.util.Iterator<File> {
	private FileFilter	directoryfilter, entryfilter;

	private Iterator<File>	itrDirectories, itrEntries;

	public DirectoryCrawler(String dirpath, final String regex) {
		directoryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				//Sometimes a directory is an entry: Do not crawl this directory
				return !entryfilter.accept(pathname) && pathname.isDirectory();
			}
		};
		entryfilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().matches(regex);
			}
		};
		itrDirectories = dirAndAllSubdirs(new File(dirpath)).iterator();
		itrEntries = Arrays.asList(itrDirectories.next().listFiles(entryfilter)).iterator();
	}

	public boolean hasNext() {
		if (itrEntries.hasNext())
			return true;

		if (itrDirectories.hasNext()) {
			itrEntries = Arrays.asList(itrDirectories.next().listFiles(entryfilter)).iterator();
			return hasNext(); //Recursion
		}

		return false;
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

	public File next() {
		if (!hasNext())
			throw new NoSuchElementException();
		return itrEntries.next();
	}

	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
