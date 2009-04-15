package crawl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

import old_model.Databank;
import old_model.Entry;


public abstract class AbstractCrawler {
	protected Databank	database;
	protected Pattern	pattern;

	public AbstractCrawler(Databank database) {
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
		Entry ef;
		for (Iterator<Entry> entritr = database.getEntries().iterator(); entritr.hasNext();) {
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
