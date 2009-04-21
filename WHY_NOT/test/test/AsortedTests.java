package test;

import java.util.List;
import java.util.TreeSet;

import model.Annotation;
import model.Databank;
import model.File;

import org.hibernate.Criteria;
import org.hibernate.Query;
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

public class AsortedTests {
	static DAOFactory	factory;
	Session				session;

	@BeforeClass
	public static void setUpClass() {
		AsortedTests.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@Before
	public void setUp() throws Exception {
		session = AsortedTests.factory.getSession();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void buildNumber() {
		Package p = getClass().getPackage();
		String version = p.getImplementationVersion();
		System.out.println(version);
	}

	//@Test
	public void criteria2() {
		Transaction transact = session.beginTransaction();
		String VALID = //
		"from File par, File chi " + //
		"where chi.databank = :child " + //
		"and par.databank = chi.databank.parent " + //
		"and par.pdbid = chi.pdbid ";
		String ANNOTATED = // + ( ... )!
		"from Annotation ann" + //
		"where ann.entry IN ";
		String UNANNOTATED = // + ( ... )!
		"from Annotation ann" + //
		"where ann.entry NOT IN ";

		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();
		Databank db = dbdao.findById("DSSP", false);

		Query q = session.createQuery("select chi " + VALID).setParameter("child", db);
		for (File ent : new TreeSet<File>(q.list()))
			System.out.println(ent);
		transact.commit();
	}

	//@Test
	@SuppressWarnings("unchecked")
	public void criteria() {
		Transaction transact = session.beginTransaction();
		Criteria crit = session.createCriteria(Annotation.class);
		crit.addOrder(Order.desc("timestamp"));
		crit.setMaxResults(10);

		for (Annotation ann : (List<Annotation>) crit.list())
			System.out.println(ann.getTimestamp());
		transact.commit();
	}

	//@Test
	public void listPDBFiles() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();
		Databank pdb = dbdao.findById("PDB", false);
		for (File file : pdb.getFiles())
			System.out.println(file);
		transact.commit();
	}

	//@Test
	public void dropHSSP() {
		Transaction transact = session.beginTransaction();
		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();
		Databank hssp = dbdao.findById("HSSP", true);
		dbdao.makeTransient(hssp);
		transact.commit();
	}

	//@Test
	public void printCounts() {
		Transaction transact = session.beginTransaction();//Plain JDBC
		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();
		AnnotationDAO anndao = AsortedTests.factory.getAnnotationDAO();

		Databank db = dbdao.findById("DSSP", false);

		System.out.println(dbdao.getValidCount(db));
		System.out.println(dbdao.getMissingCount(db));
		System.out.println(dbdao.getObsoleteCount(db));
		System.out.println(anndao.getRecent().size());

		transact.commit(); //Plain JDBC
	}
}
