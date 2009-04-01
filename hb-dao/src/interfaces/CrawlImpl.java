package interfaces;

import hello.HibernateUtil;

import java.io.File;
import java.util.List;

import model.Database;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class CrawlImpl implements ICrawl {

	public Database getDatabase(String dbname) {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		Database db = (Database) newSession.createQuery("from Database m where m.name IS :dbname").setParameter("dbname", dbname).uniqueResult();
		newTransaction.commit();
		newSession.close();
		return db;
	}

	public void addToDB(String dbname, List<File> entries) {
		// TODO Auto-generated method stub
		System.out.println("DONE");
		for (File f : entries)
			System.out.println(f.getAbsolutePath());
	}
}
