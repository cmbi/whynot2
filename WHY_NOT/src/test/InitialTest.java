package test;

import java.util.List;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;
import model.File;
import model.Databank.CrawlType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.hibernate.DAOFactory;
import dao.hibernate.HibernateUtil;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.DatabankDAO;

public class InitialTest {
	Session		session;
	DAOFactory	factory;

	@Before
	public void setUp() throws Exception {
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void storeDatabases() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = factory.getDatabankDAO();

		Databank pdb, dssp;
		dbdao.makePersistent(pdb = new Databank("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE));
		dbdao.makePersistent(dssp = new Databank("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, "ID           : ([\\d\\w]{4})", CrawlType.LINE));

		transact.commit();
	}

	@Test
	public void storeFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = factory.getDatabankDAO();
		Databank pdb = dbdao.findById("PDB", false);
		Databank dssp = dbdao.findById("DSSP", false);

		new File(pdb, "0TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "1TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "100J", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "100Q", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(dssp, "0TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(dssp, "1TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(dssp, "100J", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());

		transact.commit();
	}

	@Test
	public void storeAnnotations() {
		Transaction transact = session.beginTransaction();
		AnnotationDAO anndao = factory.getAnnotationDAO();
		DatabankDAO dbdao = factory.getDatabankDAO();

		Author author = new Author("Tim te Beek");
		Comment comment = new Comment("Example comment stored in InitialTest.java#storeAnnotations");
		Databank pdb = dbdao.findById("PDB", false);
		Databank dssp = dbdao.findById("DSSP", false);

		Entry p0TIM;
		anndao.makePersistent(new Annotation(author, comment, p0TIM = new Entry(pdb, "0TIM")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(pdb, "1TIM")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(pdb, "100J")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(pdb, "100Q")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(dssp, "0TIM")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(dssp, "1TIM")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(dssp, "100J")));
		anndao.makePersistent(new Annotation(author, comment, new Entry(dssp, "100Q")));

		transact.commit();
		System.out.println(p0TIM.getAnnotations().size());
	}

	@Test
	public void listFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = factory.getDatabankDAO();
		Databank pdb = dbdao.findById("PDB", false);
		for (File file : pdb.getFiles())
			System.out.println(file);
		transact.commit();
	}

	private void printCounts() {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		DatabankDAO dbdao = factory.getDatabankDAO();
		AnnotationDAO anndao = factory.getAnnotationDAO();

		factory.getCurrentSession().beginTransaction(); //Plain JDBC
		Databank db = dbdao.findById("DSSP", false);

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

	private void listEntries() {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		List<Entry> messages = newSession.createQuery("from EntryFile m where m.entry.database='PDB' order by m.entry.pdbid asc").list();

		System.out.println(messages.size() + " entryfile(s) found:");
		for (Entry ef : messages)
			System.out.println(ef.toString());
		newTransaction.commit();
		newSession.close();
	}

	private void deleteHSSPDB() {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction someTransaction = newSession.beginTransaction();
		Query query = newSession.createQuery("from Database m where m.name IS :dbname");
		query.setParameter("dbname", "HSSP");
		Databank db = (Databank) query.uniqueResult();
		newSession.delete(db);
		someTransaction.commit();
		newSession.close();
	}
}
