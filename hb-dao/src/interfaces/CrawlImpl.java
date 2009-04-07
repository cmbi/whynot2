package interfaces;

import hello.HibernateUtil;

import java.util.Set;

import model.Database;
import model.EntryFile;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class CrawlImpl implements ICrawl {

	public Database getDatabase(String dbname) {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		//Query query = newSession.createQuery("from Database m left join fetch m.entries where m.name IS :dbname");
		//query.setParameter("dbname", dbname);
		//Database db = (Database) query.uniqueResult();
		Database db = (Database) newSession.get(Database.class, dbname);
		int noEntries = db.getEntries().size();//Ugly lazy collection initialization
		newTransaction.commit();
		newSession.close();
		return db;
	}

	public void storeAll(Set<EntryFile> entries) {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		for (EntryFile f : entries)
			newSession.saveOrUpdate(f);
		newTransaction.commit();
		newSession.close();
	}

	public void removeAll(Set<EntryFile> entries) {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		for (EntryFile f : entries)
			newSession.delete(f);
		newTransaction.commit();
		newSession.close();
	}
}
