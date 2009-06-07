package nl.ru.cmbi.whynot.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.apache.log4j.Logger;

public class LineCrawler implements ICrawler {
	protected Databank	databank;
	protected FileDAO	filedao;
	protected Pattern	pattern;

	public LineCrawler(Databank db, FileDAO fldao) {
		databank = db;
		filedao = fldao;
		pattern = Pattern.compile(db.getRegex());
	}

	@Override
	public void addEntriesIn(java.io.File file) throws IOException {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		BufferedReader bf = new BufferedReader(new FileReader(file));

		File found = filedao.findByPathAndTimestamp(file.getAbsolutePath(), file.lastModified());
		if (found == null)
			found = new File(file);
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

				File stored = entry.getFile();

				//Create or correct file
				if (stored == null) {
					entry.setFile(found);
					added++;
				}
				else
					if (!stored.equals(found)) {
						//TODO FIXME filedao.makeTransient(entry.getFile());
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
