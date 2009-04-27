package test;

import model.Databank;
import model.File;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;
import dao.interfaces.DatabankDAO.AnnotationType;

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

	@Test
	public void printCounts() {
		Transaction transact = session.beginTransaction();//Plain JDBC
		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();

		Databank db = dbdao.findById("DSSP", false);

		System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
		System.out.println(dbdao.getValidEntries(db, AnnotationType.ALL).size());
		System.out.println(dbdao.getMissingEntries(db, AnnotationType.ALL).size());
		System.out.println(dbdao.getObsoleteEntries(db, AnnotationType.ALL).size());

		transact.commit(); //Plain JDBC
	}
}
