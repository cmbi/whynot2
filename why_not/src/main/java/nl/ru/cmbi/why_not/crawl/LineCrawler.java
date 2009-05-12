package nl.ru.cmbi.why_not.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineCrawler extends AbstractCrawler {
	@Autowired
	private FileDAO	fldao;

	@Override
	public void addEntriesIn(Databank databank, String path) throws IOException {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		java.io.File file = new java.io.File(path);
		BufferedReader bf = new BufferedReader(new FileReader(file));

		nl.ru.cmbi.why_not.model.File found = new nl.ru.cmbi.why_not.model.File(file);
		int crawled = 0, updated = 0, added = 0, index;
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = Pattern.compile(databank.getRegex()).matcher(line);
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

				nl.ru.cmbi.why_not.model.File stored = entry.getFile();

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
