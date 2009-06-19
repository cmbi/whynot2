package nl.ru.cmbi.whynot.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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

	public void crawl(java.io.File crawlfile) throws IOException {
		//Cache old Entries
		List<Entry> oldEntries = new ArrayList<Entry>(databank.getEntries());

		//File to assign to new entries
		File file = filedao.findByPathAndTimestamp(crawlfile.getAbsolutePath(), crawlfile.lastModified());
		if (file == null)
			file = new File(crawlfile);

		int added = 0;
		Matcher m;
		Scanner scn = new Scanner(crawlfile);
		while (scn.hasNextLine()) {
			//Ignore lines that do not match
			if (!(m = pattern.matcher(scn.nextLine())).matches())
				continue;
			String id = m.group(1).toLowerCase();

			//Find or create entry
			Entry entry = new Entry(databank, id);
			int oldEntryIndex = oldEntries.indexOf(entry);
			if (0 <= oldEntryIndex)
				entry = oldEntries.get(oldEntryIndex);
			else
				//Add new entry to databank
				if (databank.getEntries().add(entry))
					added++;

			//Delete annotations: We just found it!
			entry.getAnnotations().clear();
			//Set new file
			entry.setFile(file);
		}
		scn.close();

		Logger.getLogger(getClass()).info(databank.getName() + ": Adding " + added + " new Entries");
	}
}
