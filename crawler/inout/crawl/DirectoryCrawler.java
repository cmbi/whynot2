package inout.crawl;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import model.Database;
import model.Entry;

/**
 * This class provides the ablity to recursively crawl a given directory for
 * files matching FileFilter. The resulting array of MSD File objects is
 * returned by getMSDFiles().
 */
public class DirectoryCrawler implements java.util.Iterator<Entry> {
	private Database database;

	private Iterator<File> directoryIterator;

	private Iterator<Entry> entryIterator;

	public DirectoryCrawler(String filepath, Database database) {
		this.database = database;
		this.directoryIterator = this.listDirectories(new File(filepath))
				.iterator();
		this.entryIterator = this.listEntries(this.directoryIterator.next())
				.iterator();
	}

	public boolean hasNext() {
		if (this.entryIterator.hasNext())
			return true;

		if (this.directoryIterator.hasNext()) {
			this.entryIterator = this
					.listEntries(this.directoryIterator.next()).iterator();
			return this.hasNext();
		}

		return false;
	}

	public Entry next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		return this.entryIterator.next();
	}

	/**
	 * Returns list of directories composed of the argument directory together
	 * with any subdirectories recursively inside the argument directory
	 * 
	 * @param directory
	 * @return
	 */
	private List<File> listDirectories(File directory) {
		List<File> directories = new Vector<File>();
		directories.add(directory);
		// Only list directories not matching FileType (only applicable when
		// filetype matches directories (disables nesting matches))
		for (File subdir : directory.listFiles(new DirectoryFileFilter(
				this.database)))
			directories.addAll(this.listDirectories(subdir));
		return directories;
	}

	/**
	 * Returns list of MSDFiles found inside the argument directory
	 * (non-recursive)
	 * 
	 * @param directory
	 * @return
	 */
	private List<Entry> listEntries(File directory) {
		List<Entry> entries = new Vector<Entry>();
		for (File element : directory.listFiles(this.database))
			entries.add(new Entry(element, this.database));
		return entries;
	}

	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
