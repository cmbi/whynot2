package controller;

import hibernate.DAOFactory;
import hibernate.HibernateUtil;
import interfaces.DatabaseDAO;

import java.io.IOException;

import model.Database;

public class Crawler {
	private static DAOFactory	factory	= DAOFactory.instance(DAOFactory.HIBERNATE);

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
		//TODO: Supply the Session in calling getXXXDAO()
		HibernateUtil.getSessionFactory().getCurrentSession();
		DatabaseDAO dbdao = Crawler.factory.getDatabaseDAO();
		try {
			Crawler.factory.getCurrentSession().beginTransaction(); //Plain JDBC

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

			Crawler.factory.getCurrentSession().getTransaction().commit(); //Plain JDBC

			System.out.println(dbname + ": Removed " + removed + ", Added " + added);
		}
		catch (RuntimeException e) {
			Crawler.factory.getCurrentSession().getTransaction().rollback();
			throw e;
		}
		finally {
			//Close session if using anything other than current session
		}
	}
}
