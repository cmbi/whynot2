package controller;

import hibernate.DAOFactory;
import interfaces.DatabaseDAO;

import java.io.IOException;

import model.Database;

import org.hibernate.Transaction;

public class Crawler {
	public static void main(String[] args) throws IOException {
		if (args.length == 2)
			Crawler.crawl(args[0], args[1]);
		else
			;//throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
		Crawler.crawl("PDB", "/home/tbeek/Desktop/raw/");
		Crawler.crawl("DSSP", "/home/tbeek/Desktop/raw/");
		Crawler.crawl("HSSP", "/home/tbeek/Desktop/raw/");
		Crawler.crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private static void crawl(String dbname, String path) throws IOException {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

		DatabaseDAO dbdao = factory.getDatabaseDAO();
		Transaction t = dbdao.getSession().beginTransaction();

		Database db = dbdao.findById(dbname, true);
		AbstractCrawler fc;
		switch (db.getCrawltype()) {
		case FILE:
			fc = new FileCrawler(db);
			break;
		case LINE:
			fc = new LineCrawler(db);
			break;
		default:
			throw new IllegalArgumentException("Invalid CrawlType");
		}
		int removed = fc.removeInvalidEntries();
		int added = fc.addEntriesIn(path);

		t.commit();

		System.out.println(dbname + ": Removed " + removed + ", Added " + added);
	}
}
