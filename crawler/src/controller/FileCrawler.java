package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import model.Database;
import model.EntryFile;

public class FileCrawler extends AbstractCrawler {
	/**
	 * Flat file crawler
	 * @param db
	 */
	public FileCrawler(Database db) {
		super(db);
	}

	@Override
	public Set<EntryFile> getEntries(String filepath) throws IOException {
		Set<EntryFile> entries = new HashSet<EntryFile>();
		long lastmodified = new File(filepath).lastModified();
		BufferedReader bf = new BufferedReader(new FileReader(filepath));
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = pattern.matcher(line);
			if (m.matches())
				entries.add(new EntryFile(database, m.group(1), filepath, lastmodified));
			else
				;//Do nothing (obviously we don't expect ALL lines to match)
		}
		bf.close();
		return entries;
	}
}
