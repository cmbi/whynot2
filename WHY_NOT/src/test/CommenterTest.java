package test;

import java.io.File;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import comment.Commenter;

import dao.hibernate.DAOFactory;

public class CommenterTest {
	DAOFactory	factory;
	Session		session;

	@Before
	public void setUp() throws Exception {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		//factory.setSession(HibernateUtil.getSessionFactory().openSession());
		session = factory.getSession();
	}

	@After
	public void tearDown() throws Exception {
		session.close();
	}

	static final String	comfile		= "comment/com1.txt";
	static final String	uncomfile	= "uncomment/uncom1.txt";

	//@Test
	public void comment() throws Exception {
		Assert.assertTrue(Commenter.comment(CommenterTest.comfile));
	}

	@Test
	public void uncomment() throws Exception {
		Assert.assertTrue(Commenter.uncomment(CommenterTest.uncomfile));
	}

	@AfterClass
	public static void resetfiles() {
		new File(CommenterTest.comfile + ".done").renameTo(new File(CommenterTest.comfile));
		new File(CommenterTest.uncomfile + ".done").renameTo(new File(CommenterTest.uncomfile));
	}
}
