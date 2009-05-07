package dao.hibernate;

import org.hibernate.Transaction;
import org.junit.BeforeClass;

public class DAOTest {
	public static DAOFactory	factory;

	public Transaction			transaction;

	@BeforeClass
	public static void setFactory() {
		factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}
}
