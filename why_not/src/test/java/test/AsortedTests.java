package test;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

import dao.hibernate.DAOFactory;
import dao.interfaces.EntryDAO;

public class AsortedTests {
	static DAOFactory	factory;

	@BeforeClass
	public static void setUpClass() {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@Test
	public void printCounts() {
		Transaction transact = factory.getSession().beginTransaction();//Plain JDBC

		//factory.getSession().enableFilter("withFile");
		//factory.getSession().enableFilter("withoutFile");
		factory.getSession().enableFilter("withParentFile");
		//factory.getSession().enableFilter("withoutParentFile");

		factory.getSession().enableFilter("inDatabank").setParameter("name", "PDB");

		//DatabankDAO dbdao = factory.getDatabankDAO();
		//Databank pdb = dbdao.findByNaturalId(Restrictions.naturalId().set("name", "PDBFINDER"));
		//System.out.println(pdb.getEntries().size());

		EntryDAO entdao = factory.getEntryDAO();
		Logger.getLogger(AsortedTests.class).info("Before");
		Logger.getLogger(AsortedTests.class).info(entdao.count());

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
		transact.commit(); //Plain JDBC
	}
}
