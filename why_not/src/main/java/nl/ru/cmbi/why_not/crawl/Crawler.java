package nl.ru.cmbi.why_not.crawl;

import nl.ru.cmbi.why_not.hibernate.DAOFactory;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.why_not.model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;


public class Crawler {
	public static void main(String[] args) throws Exception {
		if (args.length == 2)
			new Crawler().crawl(args[0], args[1]);
		else
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");
	}

	protected static DAOFactory	factory;

	public Crawler() {
		Crawler.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	public boolean crawl(String dbname, String path) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Crawler.factory.getSession().beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = Crawler.factory.getDatabankDAO();
			Databank db = dbdao.findByNaturalId(Restrictions.naturalId().set("name", dbname));

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
			fc.addEntriesIn(path);
			fc.removeInvalidEntries();

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
