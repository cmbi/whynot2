package nl.ru.cmbi.whynot.crawl;

import java.io.IOException;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Crawler {
	public static void main(String[] args) throws Exception {
		if (args.length == 2)
			((Crawler) SpringUtil.getContext().getBean("crawler")).crawl(args[0], args[1]);
		else
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
	}

	@Autowired
	private DatabankDAO	dbdao;
	@Autowired
	private FileDAO		filedao;

	public void crawl(String dbname, String path) throws IOException {
		Databank db = dbdao.findByExample(new Databank(dbname), "id", "reference", "filelink", "parent", "regex", "crawltype", "entries");

		AbstractCrawler fc;
		switch (db.getCrawltype()) {
		case FILE:
			fc = new FileCrawler(db, filedao);
			break;
		case LINE:
			fc = new LineCrawler(db, filedao);
			break;
		default:
			throw new IllegalArgumentException("Invalid CrawlType");
		}
		fc.addEntriesIn(path);
		fc.removeInvalidEntries();

		Logger.getLogger(getClass()).info(dbname + ": Succes");
	}
}
