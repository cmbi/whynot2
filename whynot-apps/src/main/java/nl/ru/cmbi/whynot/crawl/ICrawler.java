package nl.ru.cmbi.whynot.crawl;

import java.io.IOException;

public interface ICrawler {
	/**
	 * Adds all FileEntries in the given file or directory and subdirectories to database
	 * 
	 * Extracts the PDBID from the filename/line using regular expression group matching:
	 * the PDBID should be enclosed in () and be the explicitly matching group number 1
	 * @param file
	 */
	public abstract void addEntriesIn(java.io.File file) throws IOException;
}
