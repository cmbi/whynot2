package test;

import model.Databank;
import model.File;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
	public void criteria2() {
		Transaction transact = session.beginTransaction();

		String VALID = //
		"from File par, File chi " + //
		"where chi.databank = :child " + //
		"and par.databank = chi.databank.parent " + //
		"and par.pdbid = chi.pdbid ";

		String MISSING = //
		"from File par " + //
		"where par.databank = :parent " + //
		"and (select chi.path from File chi " + //
		"where chi.databank = :child and chi.pdbid = par.pdbid ) is null ";

		String OBSOLETE = //
		"from File chi " + //
		"where chi.databank = :child " + //
		"and (select par.path from File par " + //
		"where par.databank = chi.databank.parent " + //
		"and par.pdbid = chi.pdbid ) is null ";

		String ANNCOUNT = "select count(*) from Annotation ann where chi.pdbid=ann.entry.pdbid";

		String WITH = "and (" + ANNCOUNT + ") > 0 ";

		String WITHOUT = "and (" + ANNCOUNT + ") = 0 ";

		DatabankDAO dbdao = AsortedTests.factory.getDatabankDAO();
		Databank db = dbdao.findById("DSSP", false);

		Query q = session.createQuery(OBSOLETE + WITH).setParameter("child", db);
		System.out.println(q.list().size());
		//for (File ent : new TreeSet<File>(q.list()))
		//	System.out.println(ent);
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

		//System.out.println(dbdao.getValidEntries(db).size());
		//System.out.println(dbdao.getMissingEntries(db).size());
		//System.out.println(dbdao.getObsoleteEntries(db).size());

		System.out.println(dbdao.getValidEntriesWith(db).size());
		//System.out.println(anndao.getRecent().size());

		transact.commit(); //Plain JDBC
	}
}
