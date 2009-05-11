package nl.ru.cmbi.why_not.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	private static final SessionFactory	sessionFactory;
	static {
		try {
			sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
		}
		catch (Throwable ex) {
			// Log exception!
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() throws HibernateException {
		return HibernateUtil.sessionFactory;
	}
}
