package crawl;

import java.io.IOException;
import java.util.regex.Pattern;

import model.Databank;
import model.Entry;
import model.File;
import dao.interfaces.FileDAO;

public abstract class AbstractCrawler {
	protected Databank	database;
	protected Pattern	pattern;

	public AbstractCrawler(Databank database) {
		this.database = database;
		pattern = Pattern.compile(database.getRegex());
	}

	/**
	 * Adds all FileEntries in the given file or directory and subdirectories to database
	 * 
	 * Extracts the PDBID from the filename/line using regular expression group matching:
	 * the PDBID should be enclosed in parentheses () and be the explicitly matching group 1
	 * @param path
	 */
	public abstract int addEntriesIn(String path) throws IOException;

	/**
	 * Removes all the invalid FileEntries from database by checking if the file exists
	 * and if the timestamp on the file is the same as the timestamp on the entry
	 */
	public int removeInvalidEntries() {
		FileDAO fldao = Crawler.factory.getFileDAO();

		int count = 0;
		for (Entry entry : database.getEntries()) {
			File stored = entry.getFile();
			if (stored == null)//TODO Replace null check with proper filter
				continue;
			java.io.File reference = new java.io.File(stored.getPath());
			if (!reference.exists() || reference.lastModified() != stored.getTimestamp()) {
				fldao.makeTransient(entry.getFile());
				entry.setFile(null);
				count++;
			}
		}
		return count;
	}
}
