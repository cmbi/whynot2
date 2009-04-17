package crawl;

import model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;

public class Crawler {
	protected static DAOFactory	factory	= DAOFactory.instance(DAOFactory.HIBERNATE);

	public static void main(String[] args) throws Exception {
		if (args.length == 2)
			Crawler.crawl(args[0], args[1]);
		else
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
	}

	public static boolean crawl(String dbname, String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Crawler.factory.getSession().beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = Crawler.factory.getDatabankDAO();

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
			int added = fc.addEntriesIn(path);
			int removed = fc.removeInvalidEntries();

			Logger.getLogger(Crawler.class).info(dbname + ": Adding " + added + ", Removing " + removed);

			transact.commit(); //Plain JDBC
			succes = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Crawler.class).info(dbname + ": Succes");
			else
				Logger.getLogger(Crawler.class).error(dbname + ": Failure");
		}
		return succes;
	}
}
