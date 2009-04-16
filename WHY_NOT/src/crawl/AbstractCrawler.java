package crawl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import model.Databank;

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
	 * @param path
	 */
	public abstract int addEntriesIn(String path) throws IOException;

	/**
	 * Removes all the invalid FileEntries from database by checking if the file exists
	 * and if the timestamp on the file is the same as the timestamp on the entry
	 */
	public int removeInvalidEntries() {
		Set<model.File> tbr = new HashSet<model.File>();
		for (model.File ef : database.getFiles()) {
			File file = new File(ef.getPath());
			if (!file.exists() || file.lastModified() != ef.getTime().longValue())
				tbr.add(ef);
		}

		int count = 0;
		for (model.File ef : tbr)
			if (database.getFiles().remove(ef))
				count++;
		return count;
	}

	/*
	 	public int removeInvalidEntries() {
		int count = 0;
		for (Iterator<model.File> entritr = database.getFiles().iterator(); entritr.hasNext();) {
			model.File ef = entritr.next();
			File file = new File(ef.getPath());
			if (!file.exists() || file.lastModified() != ef.getTime().longValue()) {
				entritr.remove();
				count++;
			}
		}
		return count;
	} 
	 */
}
