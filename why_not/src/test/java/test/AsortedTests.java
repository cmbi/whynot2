package test;

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
		factory.getSession().enableFilter("withFile");

		factory.getSession().enableFilter("inDatabank").setParameter("name", "PDBFINDER");

		//DatabankDAO dbdao = factory.getDatabankDAO();
		//Databank pdb = dbdao.findByNaturalId(Restrictions.naturalId().set("name", "PDBFINDER"));
		//System.out.println(pdb.getEntries().size());

		EntryDAO entdao = factory.getEntryDAO();
		System.out.println(entdao.count());

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
		transact.commit(); //Plain JDBC
	}
}
