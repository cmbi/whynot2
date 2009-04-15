package crawl;

import java.io.IOException;

import model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.hibernate.HibernateUtil;
import dao.interfaces.DatabankDAO;

public class Crawler {
	private static DAOFactory	factory	= DAOFactory.instance(DAOFactory.HIBERNATE);

	public static void main(String[] args) throws IOException {
		if (args.length == 2)
			Crawler.crawl(args[0], args[1]);
		else
			;//throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
		Crawler.crawl("PDB", "/home/tbeek/Desktop/raw/");
		Crawler.crawl("DSSP", "/home/tbeek/Desktop/raw/");
		//Crawler.crawl("HSSP", "/home/tbeek/Desktop/raw/");
		//Crawler.crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private static void crawl(String dbname, String path) throws IOException {
		//TODO: Supply the Session after calling getXXXDAO()?
		HibernateUtil.getSessionFactory().getCurrentSession();
		DatabankDAO dbdao = Crawler.factory.getDatabaseDAO();
		Transaction transact = null;
		try {
			transact = Crawler.factory.getCurrentSession().beginTransaction(); //Plain JDBC

			Databank db = dbdao.findById(dbname, true);
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

			Logger.getLogger(Crawler.class).info(dbname + ": Removing " + removed + ", Adding " + added);

			transact.commit(); //Plain JDBC

			Logger.getLogger(Crawler.class).info("Succes: " + db.getFiles().size());
		}
		catch (RuntimeException e) {
			transact.rollback();
			Logger.getLogger(Crawler.class).error("Failure");
			throw e;
		}
		finally {
			//Close session if using anything other than current session
		}
	}
}
