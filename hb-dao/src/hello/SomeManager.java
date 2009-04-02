package hello;

import java.util.List;

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

		mgr.createAndStoreStuff();

		mgr.doSomeMoreStuff();

		HibernateUtil.getSessionFactory().close();
	}

	private void doSomeMoreStuff() {
		// Second unit of work
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		List<EntryFile> messages = newSession.createQuery("from EntryFile m order by m.pdbid asc").list();

		System.out.println(messages.size() + " entryfile(s) found:");
		for (EntryFile ef : messages)
			System.out.println(ef.toString());
		newTransaction.commit();
		newSession.close();
	}

	private void createAndStoreStuff() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Author robbie;
		session.save(robbie = new Author("Robbie"));
		session.save(new Author("Tim"));
		session.save(new Author("Jurgen"));
		session.save(new Author("Script1"));
		session.save(new Author("Script2"));

		Comment example_comment;
		session.save(example_comment = new Comment("This is an example comment", robbie));
		session.save(new Comment("Another example comment", robbie));

		Database pdb, dssp, hssp;
		session.save(pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?"));
		session.save(dssp = new Database("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp"));
		session.save(hssp = new Database("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp"));
		session.save(new Database("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, ".*/PDBFIND2?\\.TXT"));

		Entry entry = new Entry(dssp, "101X");
		entry.getComments().add(example_comment);
		session.save(entry);

		session.save(new EntryFile(pdb, "101D", "/home/tbeek/Desktop/file", System.currentTimeMillis()));

		session.getTransaction().commit();
	}
}
