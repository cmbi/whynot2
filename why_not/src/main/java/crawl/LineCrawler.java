package crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;

import dao.interfaces.FileDAO;

public class LineCrawler extends AbstractCrawler {
	/**
	 * Flat file crawler
	 * @param db
	 */
	public LineCrawler(Databank db) {
		super(db);
	}

	@Override
	public void addEntriesIn(String path) throws IOException {
		FileDAO fldao = Crawler.factory.getFileDAO();

		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		java.io.File file = new java.io.File(path);
		BufferedReader bf = new BufferedReader(new FileReader(file));

		model.File found = new model.File(file);
		int crawled = 0, updated = 0, added = 0, index;
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				crawled++;

				String id = m.group(1).toLowerCase();
				//Find or create entry
				Entry entry = new Entry(databank, id);

				if (0 <= (index = oldEntries.indexOf(entry)))
					entry = oldEntries.get(index);
				else
					newEntries.add(entry);

				model.File stored = entry.getFile();

				//Create or correct file
				if (stored == null) {
					entry.setFile(found);
					added++;
				}
				else
					if (!stored.equals(found)) {
						fldao.makeTransient(entry.getFile());
						entry.setFile(found);
						updated++;
					}
			}
		}
		bf.close();
		//Add new entries to databank
		databank.getEntries().addAll(newEntries);

		Logger.getLogger(LineCrawler.class).info(databank.getName() + ": Crawled " + crawled + ", Updated " + updated + ", Added " + added);
	}
}
