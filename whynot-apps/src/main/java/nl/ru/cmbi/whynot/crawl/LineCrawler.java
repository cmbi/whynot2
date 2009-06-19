package nl.ru.cmbi.whynot.crawl;

import java.io.IOException;
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
	private Databank	databank;
	private EntryDAO	entrydao;
	private FileDAO		filedao;
	private Pattern		pattern;

	public LineCrawler(Databank db, EntryDAO entdao, FileDAO fldao) {
		databank = db;
		entrydao = entdao;
		filedao = fldao;
		pattern = Pattern.compile(db.getRegex());
	}

	public void crawl(java.io.File crawlfile) throws IOException {
		//Cache old Entries
		List<Entry> annotatedEntries = entrydao.getAnnotated(databank);
		List<Entry> presentEntries = entrydao.getPresent(databank);

		//File to assign to new entries
		File file = filedao.findByPathAndTimestamp(crawlfile.getAbsolutePath(), crawlfile.lastModified());
		if (file == null)
			file = new File(crawlfile);

		int added = 0;
		Matcher m;
		Scanner scn = new Scanner(crawlfile);
		while (scn.hasNextLine()) {
			//Skip lines that do not match
			if (!(m = pattern.matcher(scn.nextLine())).matches())
				continue;
			Entry entry = new Entry(databank, m.group(1).toLowerCase());

			//Skip present entries: No changes at this point if removeChanged ran before
			if (presentEntries.contains(entry))
				continue;

			//Find annotated entry
			int oldEntryIndex = annotatedEntries.indexOf(entry);
			if (0 <= oldEntryIndex) {
				entry = annotatedEntries.get(oldEntryIndex);
				//Delete annotations: We just found it!
				entry.getAnnotations().clear();
			}
			else
				//Add new entry to databank
				if (databank.getEntries().add(entry))
					added++;

			//Set new file
			entry.setFile(file);
		}
		scn.close();

		Logger.getLogger(getClass()).info(databank.getName() + ": Adding " + added + " new Entries");
	}
}
