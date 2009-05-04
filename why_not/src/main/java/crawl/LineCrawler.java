package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;

import model.Databank;
import model.EntryPK;
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
	public int addEntriesIn(String filepath) throws IOException {
		FileDAO fldao = Crawler.factory.getFileDAO();

		int count = 0;
		long lastmodified = new File(filepath).lastModified();
		BufferedReader bf = new BufferedReader(new FileReader(filepath));
		for (String line = ""; (line = bf.readLine()) != null;) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				String id = m.group(1).toLowerCase();
				model.File ef = fldao.findById(new EntryPK(database, id), true);
				if (ef != null) {
					ef.setPath(filepath);
					ef.setTimestamp(lastmodified);
				}
				else {
					new model.File(database, id, filepath, lastmodified);
					count++;
				}
			}
		}
		bf.close();
		return count;
	}
}