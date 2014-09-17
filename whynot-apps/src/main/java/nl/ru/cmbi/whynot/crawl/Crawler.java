package nl.ru.cmbi.whynot.crawl;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.WhynotApplication;
import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.hibernate.FileRepo;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;
import nl.ru.cmbi.whynot.model.Entry;

@Service
@Slf4j
public class Crawler {
	public static void main(final String[] args) throws Exception {
		if (args.length == 2) {
			log.info("Crawler start.");

			try (ConfigurableApplicationContext applicationContext = SpringApplication.run(WhynotApplication.class)) {
				Crawler crawler = applicationContext.getBean(Crawler.class);

				//Should run before addCrawled
				crawler.removeChanged(args[0]);

				//Should run after removeChanged
				crawler.addCrawled(args[0], args[1]);
			}

			log.info("Crawler done.");
		}
		else
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
	}

	@Autowired
	private DatabankRepo	dbdao;
	@Autowired
	private EntryRepo		entrydao;
	@Autowired
	private FileRepo		filedao;

	/**
	 * Removes entries from databank if <li>file on path does not exist <li>timestamp differs from timestamp of file on
	 * path <li>path does not match databank regex (which might have changed) <li>no file or parent entry file exists
	 *
	 * @param name
	 */
	@Transactional
	public void removeChanged(final String name) {
		Databank databank = dbdao.findByName(name);
		Pattern regex = Pattern.compile(databank.getRegex());
		boolean matchRegex = databank.getCrawltype() == CrawlType.FILE;
		int removed = 0;
		for (Entry entry : entrydao.getPresent(databank)) {
			String path = entry.getFile().getPath();
			File file = new File(path);
			//Check if file still exists
			if (!file.exists() || file.lastModified() != entry.getFile().getTimestamp() ||
					//Check if file still matches regex
					matchRegex && !regex.matcher(path).matches()) {
				//Remove entry
				databank.getEntries().remove(entry);
				entrydao.delete(entry);
				removed++;
			}
		}
		for (Entry entry : entrydao.getObsolete(databank))
			if (entry.getFile() == null) {
				//Remove entry
				databank.getEntries().remove(entry);
				entrydao.delete(entry);
				removed++;
			}
		log.info(databank.getName() + ": Removing " + removed + " changed Entries");
	}

	/**
	 * Adds all FileEntries in the given file or directory and subdirectories to database. Takes great care to delete
	 * old files when possible and to clear present annotations. <br/>
	 * <br/>
	 * Extracts the PDBID from the filename/line using regular expression group matching: the PDBID should be enclosed
	 * in () and be the explicitly matching group number 1 Note: Strongly expects removeChanged to have run before
	 *
	 * @param dbname
	 * @param path
	 * @throws IOException
	 */
	@Transactional
	public void addCrawled(final String dbname, final String path) throws IOException {
		Databank db = dbdao.findByName(dbname);
		switch (db.getCrawltype()) {
		case FILE:
			new FileCrawler(db, entrydao).crawl(getFile(path));
			break;
		case LINE:
			new LineCrawler(db, entrydao, filedao).crawl(getFile(path));
			break;
		default:
			throw new IllegalArgumentException("Invalid CrawlType");
		}
	}

	/**
	 * Gets the file on the supplied path. If the path starts with http:// we first store a local copy with the same
	 * timestamp and return that.
	 *
	 * @param path
	 * @return the file on the supplied path
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static File getFile(final String path) throws IOException, MalformedURLException {
		if (path.startsWith("http://")) {
			File dirDownload = new File("download/");
			//Make sure download directory exist
			if (!dirDownload.isDirectory() && !dirDownload.mkdir())
				throw new FileNotFoundException(dirDownload.getAbsolutePath());

			//Open URL
			URLConnection con = new URL(path).openConnection();
			String cache = path.substring(path.lastIndexOf('/') + 1);
			cache = cache.replaceAll("[^\\w]", "");
			File downloaded = new File("download/" + cache);
			if (!downloaded.exists() || downloaded.lastModified() != con.getLastModified()) {
				//Overwrite file
				BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
				PrintWriter pw = new PrintWriter(new FileWriter(downloaded));
				String line;
				while ((line = bf.readLine()) != null)
					pw.println(line);
				pw.close();
				bf.close();
				downloaded.setLastModified(con.getLastModified());
				log.info("Downloaded " + downloaded.getAbsolutePath());
			}
			cache = downloaded.getAbsolutePath();
			return new File(cache);
		}
		return new File(path);
	}
}
