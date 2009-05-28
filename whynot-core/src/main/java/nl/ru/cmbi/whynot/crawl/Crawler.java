package nl.ru.cmbi.whynot.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		fc.addEntriesIn(getFile(path));
		fc.removeInvalidEntries();

		Logger.getLogger(getClass()).info(dbname + ": Succes");
	}

	private static File getFile(String path) throws IOException, MalformedURLException {
		if (path.startsWith("http://")) {
			File dirDownload = new File("download/");
			//Make sure download directory exist
			if (!dirDownload.isDirectory() && !dirDownload.mkdir())
				throw new FileNotFoundException(dirDownload.getAbsolutePath());

			//Open URL
			URLConnection con = new URL(path).openConnection();
			File downloaded = new File("download/" + path.replaceAll("[^\\w]", ""));
			if (!downloaded.exists() || downloaded.lastModified() != con.getLastModified()) {
				//Overwrite file
				BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
				PrintWriter pw = new PrintWriter(new FileWriter(downloaded));
				String line;
				while ((line = bf.readLine()) != null)
					pw.println(line);
				pw.close();
				downloaded.setLastModified(con.getLastModified());
				Logger.getLogger(Crawler.class).info("Downloaded " + downloaded.getAbsolutePath());
			}
			path = downloaded.getAbsolutePath();
		}
		return new File(path);
	}
}
