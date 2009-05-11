package dao.hibernate;

import model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

import dao.interfaces.GenericDAO.EntryDAO;

public class FilterTest {
	static DAOFactory	factory;

	@BeforeClass
	public static void setUpClass() {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	@Test
	public void printCounts() {
		Transaction transact = factory.getSession().beginTransaction();//Plain JDBC

		//factory.getSession().enableFilter("inDatabank").setParameter("name", "DSSP");

		//factory.getSession().enableFilter("withFile");
		//factory.getSession().enableFilter("withoutFile");

		//factory.getSession().enableFilter("withParentFile");
		//factory.getSession().enableFilter("withoutParentFile");

		//factory.getSession().enableFilter("withComment");
		//factory.getSession().enableFilter("withoutComment");
		//factory.getSession().enableFilter("withOlderComment");
		factory.getSession().enableFilter("withComment:").setParameter("comment", "Another new example comment from com1.txt");

		EntryDAO entdao = factory.getEntryDAO();
		Logger.getLogger(FilterTest.class).info("Before");
		Logger.getLogger(FilterTest.class).info(entdao.count());
		for (Entry entry : entdao.findAll())
			Logger.getLogger(FilterTest.class).info(entry);

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
		transact.commit(); //Plain JDBC
	}
}
