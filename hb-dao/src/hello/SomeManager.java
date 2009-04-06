package hello;

import java.util.List;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Database;
import model.Entry;
import model.EntryFile;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SomeManager {

	public static void main(String[] args) {
		SomeManager mgr = new SomeManager();

		Session newSession = HibernateUtil.getSessionFactory().openSession();
		newSession.close();

		//mgr.doSomeStuffFirst();

		mgr.createAndStoreStuff();

		mgr.doSomeMoreStuff();

		HibernateUtil.getSessionFactory().close();
	}

	private void doSomeStuffFirst() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Database pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?");
		Comment comment = new Comment("Example comment");
		Author author = new Author("Robbie");
		Entry entry = new Entry(pdb, "0TIM");
		EntryFile entryfile = new EntryFile(entry, "/home/tbeek/Desktop/somefile", System.currentTimeMillis());
		session.save(entryfile);
		session.save(new Annotation(entry, comment, author));

		session.getTransaction().commit();
		session.close();
	}

	private void doSomeMoreStuff() {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		List<EntryFile> messages = newSession.createQuery("from EntryFile m where m.entryPK.database='PDB' order by m.entryPK.pdbid asc").list();

		System.out.println(messages.size() + " entryfile(s) found:");
		for (EntryFile ef : messages)
			System.out.println(ef.toString());
		newTransaction.commit();
		newSession.close();
	}

	private void createAndStoreStuff() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Database pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?"), dssp, hssp;
		session.save(pdb);
		session.save(dssp = new Database("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp"));
		session.save(hssp = new Database("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp"));
		session.save(new Database("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, ".*/PDBFIND2?\\.TXT"));

		Comment comment = new Comment("Example comment");
		session.save(comment);

		Author author = new Author("Robbie");
		session.save(author);
		session.save(new Author("Script1"));
		session.save(new Author("Script2"));
		
		Entry entry;
		session.save(entry = new Entry(pdb, "0TIM"));

		EntryFile entryfile;
		session.save(entryfile = new EntryFile(entry, "/home/tbeek/Desktop/somefile", System.currentTimeMillis()));
		//session.save(entry = new Entry(pdb, "0TIM"));

		session.save(new Annotation(entry, comment, author));
		//Only works if accessible from already persistent instance
		//session.save(new Annotation(new Entry(pdb, "0TIM"), new Comment("My new comment"), new Author("Tim")));
		//So this works:
		//dssp.getEntries().add(new EntryFile(dssp, "0TIM", "/some/other/path", System.currentTimeMillis()));

		session.getTransaction().commit();
	}
}
