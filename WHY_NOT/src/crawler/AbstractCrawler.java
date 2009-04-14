package crawler;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
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
	 * Adds all FileEntries in the given file or directory and subdirectories to database
	 * <br/><br/>
	 * Extracts the PDBID from the filename/line using regular expression group matching:
	 * the PDBID should be enclosed in parentheses () and be the explicitly matching group 1
	 * @param dirpath
	 * @return set&lt;EntryFile&gt;
	 */
	public abstract int addEntriesIn(String path) throws IOException;

	/**
	 * Removes all the invalid FileEntries from database by checking if the file exists
	 * and if the timestamp on the file is the same as the timestamp on the entry
	 */
	public int removeInvalidEntries() {
		int count = 0;
		EntryFile ef;
		for (Iterator<EntryFile> entritr = database.getEntries().iterator(); entritr.hasNext();) {
			ef = entritr.next();
			File file = new File(ef.getPath());
			if (!file.exists() || file.lastModified() != ef.getLastmodified()) {
				entritr.remove();
				count++;
			}
		}
		return count;
	}
}
