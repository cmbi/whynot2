package nl.ru.cmbi.why_not.hibernate;


import nl.ru.cmbi.why_not.hibernate.DAOFactory;

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
