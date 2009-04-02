package interfaces;

import hello.HibernateUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Database;
import model.EntryFile;

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
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		Database db = (Database) newSession.createQuery("from Database m where m.name IS :dbname").setParameter("dbname", dbname).uniqueResult();

		Pattern p = Pattern.compile(db.getRegex());
		Matcher m;
		String pdbid;
		for (File f : entries) {
			m = p.matcher(f.getAbsolutePath());
			if (m.matches()) {
				pdbid = m.group(1);
				newSession.persist(new EntryFile(db, pdbid, f.getAbsolutePath(), f.lastModified()));
			}
		}
		newTransaction.commit();
		newSession.close();
	}
}
