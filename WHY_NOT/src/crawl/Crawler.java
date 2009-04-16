package crawl;

import java.io.IOException;

import model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;

public class Crawler {

	public static void main(String[] args) throws IOException {
		if (args.length == 2)
			Crawler.crawl(args[0], args[1]);
		else
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
	}

	public static boolean crawl(String dbname, String path) throws IOException {
		DAOFactory factory;
		Session session;
		Transaction transact = null;
		//TODO: Supply the Session after calling getXXXDAO()?
		boolean succes = false;
		try {
			factory = DAOFactory.instance(DAOFactory.HIBERNATE);
			session = factory.getCurrentSession();
			transact = session.beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = factory.getDatabankDAO();

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
			int newtotal = fc.addEntriesIn(path);

			Logger.getLogger(Crawler.class).info(dbname + ": Removing " + removed + ", New total " + newtotal);

			transact.commit(); //Plain JDBC
			succes = true;
		}
		catch (RuntimeException e) {
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Crawler.class).info("Succes");
			else
				Logger.getLogger(Crawler.class).error("Failure");
		}
		return succes;
	}
}
