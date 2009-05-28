package nl.ru.cmbi.whynot.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.log4j.Logger;

public class LineCrawler extends AbstractCrawler {
	public LineCrawler(Databank databank, FileDAO filedao) {
		super(databank, filedao);
	}

	@Override
	public void addEntriesIn(java.io.File file) throws IOException {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		BufferedReader bf = new BufferedReader(new FileReader(file));

		nl.ru.cmbi.whynot.model.File found = new nl.ru.cmbi.whynot.model.File(file);
		int crawled = 0, updated = 0, added = 0, index;
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				crawled++;

				String id = m.group(1).toLowerCase();

				//Find or create entry
				Entry entry = new Entry(databank, id);
				if (0 <= (index = oldEntries.indexOf(entry))) {
					entry = oldEntries.get(index);
					entry.getAnnotations().clear();
				}
				else
					newEntries.add(entry);

				nl.ru.cmbi.whynot.model.File stored = entry.getFile();

				//Create or correct file
				if (stored == null) {
					entry.setFile(found);
					added++;
				}
				else
					if (!stored.equals(found)) {
						filedao.makeTransient(entry.getFile());
						entry.setFile(found);
						updated++;
					}
			}
		}
		bf.close();
		//Add new entries to databank
		databank.getEntries().addAll(newEntries);

		Logger.getLogger(getClass()).info(databank.getName() + ": Crawled " + crawled + ", Updated " + updated + ", Added " + added);
	}
}
