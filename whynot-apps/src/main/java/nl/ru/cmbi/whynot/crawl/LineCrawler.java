package nl.ru.cmbi.whynot.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.apache.log4j.Logger;

public class LineCrawler {
	protected Databank	databank;
	protected EntryDAO	entrydao;
	protected FileDAO	filedao;
	protected Pattern	pattern;

	public LineCrawler(Databank db, EntryDAO entdao, FileDAO fldao) {
		databank = db;
		entrydao = entdao;
		filedao = fldao;
		pattern = Pattern.compile(db.getRegex());
	}

	public void crawl(java.io.File file) throws IOException {
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());
		List<Entry> newEntries = new ArrayList<Entry>();

		BufferedReader bf = new BufferedReader(new FileReader(file));

		File found = filedao.findByPathAndTimestamp(file.getAbsolutePath(), file.lastModified());
		if (found == null)
			found = new File(file);
		int crawled = 0, updated = 0, added = 0, index;
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = pattern.matcher(line);
			if (!m.matches())
				continue;
			crawled++;
			String id = m.group(1).toLowerCase();

			//Find or create entry
			Entry entry = new Entry(databank, id);
			if (0 <= (index = oldEntries.indexOf(entry)))
				entry = oldEntries.get(index);
			else
				newEntries.add(entry);

			//Find or create file
			File stored = entry.getFile();
			if (found.equals(stored))
				continue;//We're done

			//Set new file
			entry.setFile(found);
			//Delete annotations: We just found it!
			entry.getAnnotations().clear();

			if (stored == null)
				added++;
			else {
				//Delete old file (if no longer used)
				;//if (entrydao.countAllWith(stored) == 0)
				;//	filedao.makeTransient(stored);
				updated++;
			}

		}
		bf.close();
		//Add new entries to databank
		databank.getEntries().addAll(newEntries);

		Logger.getLogger(getClass()).info(databank.getName() + ": Crawled " + crawled + ", Updated " + updated + ", Added " + added);
	}
}
