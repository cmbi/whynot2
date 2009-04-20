package test;

import java.util.List;

import model.Annotation;
import model.Databank;
import model.File;

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
