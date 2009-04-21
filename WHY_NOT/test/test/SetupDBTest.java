package test;

import java.util.Iterator;

import model.Annotation;
import model.Author;
import model.Comment;
import model.Databank;
import model.Entry;
import model.File;
import model.Databank.CrawlType;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;

public class SetupDBTest {
	static DAOFactory	factory;
	Session				session;

	@BeforeClass
	public static void setUpClass() {
		AnnotationConfiguration cfg = new AnnotationConfiguration().configure();
		new SchemaExport(cfg).drop(true, true);
		new SchemaExport(cfg).create(true, true);
		SetupDBTest.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@Before
	public void setUp() throws Exception {
		session = SetupDBTest.factory.getSession();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void storeDatabaseTest() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = SetupDBTest.factory.getDatabankDAO();

		dbdao.makePersistent(new Databank("TEST", "ref", "link", null, "regex", CrawlType.FILE));

		transact.commit();
	}

	@Test
	public void storeFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = SetupDBTest.factory.getDatabankDAO();
		Databank test = dbdao.findById("TEST", true);
		new File(test, "0001", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		new File(test, "0002", "/home/tbeek/Desktop/raw/stats", System.currentTimeMillis());
		transact.commit();
	}

	@Test
	public void storeAnnotations() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = SetupDBTest.factory.getDatabankDAO();

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
	public void dropFile() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = SetupDBTest.factory.getDatabankDAO();
		Databank pdb = dbdao.findById("TEST", true);

		Iterator<File> itr = pdb.getFiles().iterator();
		File fl = itr.next();
		System.out.println(fl);
		itr.remove();

		transact.commit();
	}
}
