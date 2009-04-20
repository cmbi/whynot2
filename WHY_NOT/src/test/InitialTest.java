package test;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import model.Annotation;
import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;
import model.File;
import model.Databank.CrawlType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dao.hibernate.DAOFactory;
import dao.interfaces.AnnotationDAO;
import dao.interfaces.DatabankDAO;

public class InitialTest {
	static DAOFactory	factory;
	Session				session;

	@BeforeClass
	public static void setUpClass() {
		InitialTest.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@Before
	public void setUp() throws Exception {
		session = InitialTest.factory.getSession();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void storeDatabases() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();

		Databank pdb, dssp;
		dbdao.makePersistent(new Databank("TEST", "ref", "link", null, "regex", CrawlType.FILE));
		dbdao.makePersistent(pdb = new Databank("PDB", "pdb.org", "google.com/?q=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE));
		dbdao.makePersistent(dssp = new Databank("DSSP", "dssp.org", "google.com/?q=", pdb, ".*/([\\d\\w]{4})\\.dssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("HSSP", "hssp.org", "google.com/?q=", dssp, ".*/([\\d\\w]{4})\\.hssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDBFINDER", "pdbfinder.org", "google.com/?q=", pdb, "ID           : ([\\d\\w]{4})", CrawlType.LINE));
		Assert.assertEquals(dbdao.findAll().size(), 5);

		transact.commit();
	}

	@Test
	public void storeFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();
		Databank test = dbdao.findById("TEST", true);
		new File(test, "0001", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(test, "0002", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());

		Databank pdb = dbdao.findById("PDB", true);
		Databank dssp = dbdao.findById("DSSP", true);
		Databank hssp = dbdao.findById("HSSP", true);

		new File(pdb, "0TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "1TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "100J", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(pdb, "100Q", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		//Assert.assertEquals(pdb.getFiles().size(), 4);

		new File(dssp, "0TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(dssp, "1TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(dssp, "100J", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		//Assert.assertEquals(dssp.getFiles().size(), 3);

		new File(hssp, "0TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(hssp, "1TIM", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		//Assert.assertEquals(hssp.getFiles().size(), 2);

		transact.commit();
	}

	@Test
	public void storeAnnotations() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();

		Author author = new Author("Tim te Beek");
		Comment comment = new Comment("Example comment stored in InitialTest.java");
		Databank test = dbdao.findById("TEST", true);
		Entry entry = new Entry(test, "0001");
		new Annotation(author, comment, entry, 1L);

		Databank pdb = dbdao.findById("PDB", true);
		Databank dssp = dbdao.findById("DSSP", true);
		Databank hssp = dbdao.findById("HSSP", true);

		new Annotation(author, comment, new Entry(pdb, "0TIM"));
		new Annotation(author, comment, new Entry(pdb, "1TIM"));
		new Annotation(author, comment, new Entry(pdb, "100J"));
		new Annotation(author, comment, new Entry(pdb, "100Q"));

		new Annotation(author, comment, new Entry(dssp, "0TIM"));
		new Annotation(author, comment, new Entry(dssp, "1TIM"));
		new Annotation(author, comment, new Entry(dssp, "100J"));
		new Annotation(author, comment, new Entry(dssp, "100Q"));

		new Annotation(author, comment, new Entry(hssp, "0TIM"));
		new Annotation(author, comment, new Entry(hssp, "1TIM"));
		new Annotation(author, comment, new Entry(hssp, "100J"));
		new Annotation(author, comment, new Entry(hssp, "100Q"));

		transact.commit();
	}

	@Test
	public void listPDBFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();
		Databank pdb = dbdao.findById("PDB", false);
		for (File file : pdb.getFiles())
			System.out.println(file);
		transact.commit();
	}

	@Test
	public void dropFile() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();
		Databank pdb = dbdao.findById("TEST", true);

		Iterator<File> itr = pdb.getFiles().iterator();
		File fl = itr.next();
		System.out.println(fl);
		itr.remove();

		transact.commit();
	}

	//@Test
	public void dropHSSP() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();
		Databank hssp = dbdao.findById("HSSP", true);
		dbdao.makeTransient(hssp);
		transact.commit();
	}

	@Test
	public void printCounts() {
		Transaction transact = session.beginTransaction();//Plain JDBC
		DatabankDAO dbdao = InitialTest.factory.getDatabankDAO();
		AnnotationDAO anndao = InitialTest.factory.getAnnotationDAO();

		Databank db = dbdao.findById("DSSP", false);

		System.out.println(dbdao.getValidCount(db));
		System.out.println(dbdao.getMissingCount(db));
		System.out.println(dbdao.getObsoleteCount(db));
		System.out.println(anndao.getRecent().size());

		transact.commit(); //Plain JDBC
	}

	@Test
	public void criteria() {
		Transaction transact = session.beginTransaction();
		Criteria crit = session.createCriteria(Annotation.class);
		crit.addOrder(Order.desc("timestamp"));
		crit.setMaxResults(10);

		for (Annotation ann : (List<Annotation>) crit.list())
			System.out.println(ann);
		transact.commit();
	}
}
