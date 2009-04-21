package dao.implementations;

import java.util.List;

import model.Annotation;
import model.AnnotationPK;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import dao.interfaces.AnnotationDAO;

public class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, AnnotationPK> implements AnnotationDAO {
	@SuppressWarnings("unchecked")
	public List<Annotation> getRecent() {
		Criteria crit = getSession().createCriteria(Annotation.class);
		crit.addOrder(Order.desc("timestamp"));
		crit.setMaxResults(10);
		return crit.list();
	}
}
