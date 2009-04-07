package controller;

import interfaces.CrawlImpl;
import interfaces.ICrawl;

import java.io.IOException;

import model.Database;

public class Crawler {

	public static void main(String[] args) throws IOException {
		if (args.length == 2)
			Crawler.crawl(args[0], args[1]);
		else
			;//throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
		Crawler.crawl("PDB", "/home/tbeek/Desktop/raw/");
		//Crawler.crawl("DSSP", "/home/tbeek/Desktop/raw/");
		//Crawler.crawl("HSSP", "/home/tbeek/Desktop/raw/");
		Crawler.parse("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private static void crawl(String dbname, String path) {
		ICrawl dao = new CrawlImpl();
		Database db = dao.getDatabase(dbname);
		DirCrawler fc = new DirCrawler(db);
		dao.removeAll(fc.getInvalidEntries(db.getEntries()));
		dao.storeAll(fc.getEntries(path));
	}

	private static void parse(String dbname, String path) throws IOException {
		ICrawl dao = new CrawlImpl();
		Database db = dao.getDatabase(dbname);
		FileCrawler fc = new FileCrawler(db);
		dao.removeAll(fc.getInvalidEntries(db.getEntries()));
		dao.storeAll(fc.getEntries(path));
	}
}
