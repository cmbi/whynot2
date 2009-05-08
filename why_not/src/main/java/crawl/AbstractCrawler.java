package crawl;

import java.io.IOException;
import java.util.regex.Pattern;

import model.Databank;
import model.Entry;
import model.File;

import org.apache.log4j.Logger;

import dao.interfaces.FileDAO;

public abstract class AbstractCrawler {
	protected Databank	databank;
	protected Pattern	pattern;

	public AbstractCrawler(Databank database) {
		databank = database;
		pattern = Pattern.compile(database.getRegex());
	}

	/**
	 * Adds all FileEntries in the given file or directory and subdirectories to database
	 * 
	 * Extracts the PDBID from the filename/line using regular expression group matching:
	 * the PDBID should be enclosed in parentheses () and be the explicitly matching group 1
	 * @param path
	 */
	public abstract void addEntriesIn(String path) throws IOException;

	/**
	 * Removes all the invalid FileEntries from database by checking if the file exists
	 * and if the timestamp on the file is the same as the timestamp on the entry
	 */
	public void removeInvalidEntries() {
		FileDAO fldao = Crawler.factory.getFileDAO();

		Crawler.factory.getSession().enableFilter("withFile");

		int checked = 0, removed = 0;
		for (Entry entry : databank.getEntries()) {
			File stored = entry.getFile();
			if (stored == null)//Should not happen because of above filter
				continue;//But to be safe, we'll skip just like we used to
			checked++;
			java.io.File found = new java.io.File(stored.getPath());
			if (!found.exists() || found.lastModified() != stored.getTimestamp()) {
				fldao.makeTransient(entry.getFile());
				entry.setFile(null);
				removed++;
			}
		}

		Crawler.factory.getSession().disableFilter("withFile");

		Logger.getLogger(AbstractCrawler.class).info(databank.getName() + ": Checked " + checked + ", Removed " + removed);
	}
}
