package controller;

import interfaces.CrawlImpl;
import interfaces.ICrawl;
import io.FileCrawler;

import java.io.File;
import java.util.Iterator;

import model.Database;
import model.EntryFile;

public class Crawler {

	public static void main(String[] args) {
		new Crawler(args);
	}

	public Crawler(String[] args) {
		if (args.length == 2)
			crawl(args[0], args[1]);
		else
			;//throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");

		//crawl("PDB", "/home/tbeek/Desktop/raw/");
		//crawl("DSSP", "/home/tbeek/Desktop/raw/");
		//crawl("HSSP", "/home/tbeek/Desktop/raw/");

		verify("PDB");
		//verify("DSSP");
		//verify("HSSP");
	}

	private void crawl(String database, String path) {
		ICrawl dao = new CrawlImpl();
		Database db = dao.retrieveDatabase(database);
		dao.storeAll(new FileCrawler(db).getEntries(path));
	}

	private void verify(String database) {
		ICrawl dao = new CrawlImpl();
		Database db = dao.retrieveDatabase(database);

		File fl;
		EntryFile ef;
		Iterator<EntryFile> efItr = db.getEntries().iterator();
		while (efItr.hasNext()) {
			ef = efItr.next();
			fl = new File(ef.getPath());
			if (!fl.isFile())
				efItr.remove();
			else
				if (fl.lastModified() != ef.getLastmodified())
					ef.setLastmodified(fl.lastModified());
		}

		dao.update(db);
	}
}
