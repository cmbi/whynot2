package fill;

import java.util.List;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Database;
import model.EntryFile;
import model.Database.CrawlType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.hibernate.HibernateUtil;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.DatabaseDAO;

public class Filler {

	public static void main(String[] args) {
		Filler mgr = new Filler();

		//mgr.fillTabels();

		//mgr.storeFileAndComment();

		//mgr.listEntries();

		//mgr.deleteHSSPDB();

		mgr.unrelatedTest();

		HibernateUtil.getSessionFactory().close();
	}

	private void unrelatedTest() {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		DatabaseDAO dbdao = factory.getDatabaseDAO();
		AnnotationDAO anndao = factory.getAnnotationDAO();

		factory.getCurrentSession().beginTransaction(); //Plain JDBC
		Database db = dbdao.findById("DSSP", false);

		//System.out.println(dbdao.getMissingCount(db));
		System.out.println(dbdao.getValidCount(db));
		System.out.println(dbdao.getMissingCount(db));
		System.out.println(dbdao.getObsoleteCount(db));
		System.out.println(anndao.getRecent().size());

		factory.getCurrentSession().getTransaction().commit(); //Plain JDBC

		//		Session newSession = HibernateUtil.getSessionFactory().openSession();
		//		Transaction newTransaction = newSession.beginTransaction();
		//
		//		Database db = (Database) newSession.get(Database.class, "DSSP");
		//		EntryFile ef = (EntryFile) newSession.get(EntryFile.class, new EntryPK(db, "0TIM"));
		//		db.getEntries().remove(ef);
		//
		//		newTransaction.commit();
		//		newSession.close();
	}

	private void storeFileAndComment() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = session.beginTransaction();

		Database pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE);
		Comment comment = new Comment("Example comment");
		Author author = new Author("Robbie");
		session.saveOrUpdate(new EntryFile(pdb, "0TIM", "/home/tbeek/Desktop/somefile", System.currentTimeMillis()));
		session.saveOrUpdate(new Annotation(pdb, "0TIM", comment, author));

		newTransaction.commit();
		session.close();
	}

	private void listEntries() {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		List<EntryFile> messages = newSession.createQuery("from EntryFile m where m.entry.database='PDB' order by m.entry.pdbid asc").list();

		System.out.println(messages.size() + " entryfile(s) found:");
		for (EntryFile ef : messages)
			System.out.println(ef.toString());
		newTransaction.commit();
		newSession.close();
	}

	private void fillTabels() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Database pdb = new Database("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE), dssp, hssp;
		session.save(pdb);
		session.save(dssp = new Database("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp", CrawlType.FILE));
		session.save(hssp = new Database("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp", CrawlType.FILE));
		session.save(new Database("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, "ID           : ([\\d\\w]{4})", CrawlType.LINE));

		Comment comment = new Comment("Example comment");
		session.save(comment);

		Author author = new Author("Robbie");
		session.save(author);
		session.save(new Author("Script1"));
		session.save(new Author("Script2"));

		EntryFile entry = new EntryFile(pdb, "0TIM", "/home/tbeek/Desktop/somefile", System.currentTimeMillis());
		session.save(entry);

		session.save(new Annotation(pdb, "0TIM", comment, author));
		//Only works if accessible from already persistent instance
		//session.save(new Annotation(new Entry(pdb, "0TIM"), new Comment("My new comment"), new Author("Tim")));
		//So this works:
		//dssp.getEntries().add(new EntryFile(dssp, "0TIM", "/some/other/path", System.currentTimeMillis()));

		session.getTransaction().commit();
	}

	private void deleteHSSPDB() {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction someTransaction = newSession.beginTransaction();
		Query query = newSession.createQuery("from Database m where m.name IS :dbname");
		query.setParameter("dbname", "HSSP");
		Database db = (Database) query.uniqueResult();
		newSession.delete(db);
		someTransaction.commit();
		newSession.close();
	}
}
