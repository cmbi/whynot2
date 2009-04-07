package interfaces;

import hello.HibernateUtil;

import java.util.Set;

import model.Annotation;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class AnnotateImpl implements IAnnotate {
	public void storeAll(Set<Annotation> annotations) {
		Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction newTransaction = newSession.beginTransaction();
		for (Annotation a : annotations)
			newSession.saveOrUpdate(a);
		newTransaction.commit();
		newSession.close();
	}
}
