package controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import model.Database;
import model.EntryFile;

public abstract class AbstractCrawler {
	protected Database	database;
	protected Pattern	pattern;

	public AbstractCrawler(Database database) {
		this.database = database;
		pattern = Pattern.compile(database.getRegex());
	}

	/**
	 * Gets all the FileEntries for the supplied database in the given file or directory
	 * and any subdirectories
	 * <br/><br/>
	 * Extracts the PDBID from the filename/line using regular expression group matching:
	 * the PDBID should be enclosed in parentheses () and be the explicitly matching group 1
	 * @param dirpath
	 * @return set&lt;EntryFile&gt;
	 */
	public abstract Set<EntryFile> getEntries(String filepath) throws Exception;

	/**
	 * Gets all the invalid FileEntries from entries by checking if the file exists
	 * and if the timestamp on the entry is the same as the timestamp on the file
	 * @param entries
	 * @return set&lt;EntryFile&gt;
	 */
	public Set<EntryFile> getInvalidEntries(Set<EntryFile> entries) {
		Set<EntryFile> invalids = new HashSet<EntryFile>();
		for (EntryFile ef : entries) {
			File file = new File(ef.getPath());
			if (!file.exists() || file.lastModified() != ef.getLastmodified())
				invalids.add(ef);
		}
		return invalids;
	}
}
