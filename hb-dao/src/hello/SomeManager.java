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

		mgr.doSomeStuffFirst();

		mgr.createAndStoreStuff();

		mgr.doSomeMoreStuff();

		HibernateUtil.getSessionFactory().close();
	}

	private void doSomeStuffFirst() {
	//		System.exit(1);
	//		List<EntryFile> messages = new ArrayList<EntryFile>();
	//		while (true) {
	//			messages.add(new EntryFile(new Database("", "", "", null, ""), "", "", 0));
	//			if (messages.size() % 1000 == 0)
	//				System.out.println(messages.size());
	//		}
	}

	private void doSomeMoreStuff() {
		// Second unit of work
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

		Database pdb, dssp, hssp;
		session.save(pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?"));
		session.save(dssp = new Database("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp"));
		session.save(hssp = new Database("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp"));
		session.save(new Database("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, ".*/PDBFIND2?\\.TXT"));

		Comment comment;
		session.save(comment = new Comment("Example comment"));

		Author author;
		session.save(author = new Author("Robbie"));
		session.save(new Author("Script1"));
		session.save(new Author("Script2"));

		Entry entry;
		session.save(entry = new Entry(dssp, "101X"));

		session.save(new Annotation(entry, comment, author));
		//session.save(new Annotation(new Entry(pdb, "0TIM"), new Comment("My new comment"), new Author("Tim")));
		//Only works if accessible from already persistent instance

		session.save(new EntryFile(pdb, "101D", "/home/tbeek/Desktop/somefile", System.currentTimeMillis()));

		session.getTransaction().commit();
	}
}
