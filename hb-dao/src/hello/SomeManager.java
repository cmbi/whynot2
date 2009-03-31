package hello;

import java.util.Calendar;
import java.util.Date;

import model.Author;
import model.Comment;
import model.Database;
import model.Entry;
import model.EntryFile;

import org.hibernate.Session;

public class SomeManager {

	public static void main(String[] args) {
		SomeManager mgr = new SomeManager();

		mgr.createAndStoreStuff();

		HibernateUtil.getSession().close();
	}

	private void createAndStoreStuff() {

		Session session = HibernateUtil.getSession().getCurrentSession();
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
		session.save(pdb = new Database("PDB", "", "", null, ""));
		session.save(dssp = new Database("DSSP", "", "", pdb, ""));
		session.save(hssp = new Database("HSSP", "", "", dssp, ""));
		session.save(new Database("PDBFINDER", "", "", pdb, ""));

		Entry entry = new Entry(dssp, "101X");
		entry.getComments().add(example_comment);
		session.save(entry);

		Date tm = Calendar.getInstance().getTime();

		session.save(new EntryFile(pdb, "101D", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(pdb, "101M", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(pdb, "101Z", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(pdb, "101X", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(dssp, "101M", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(dssp, "101D", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(dssp, "101Y", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(hssp, "101M", "/home/tbeek/Desktop/file", tm));
		session.save(new EntryFile(hssp, "101D", "/home/tbeek/Desktop/file", tm));

		session.getTransaction().commit();
	}
}
