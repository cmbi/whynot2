package dao.implementations;

import java.util.List;


import old_model.Annotation;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import dao.interfaces.AnnotationDAO;

public class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Integer> implements AnnotationDAO {
	private static final String	ANNOTATED	= // + ( ... )!
											"from Annotation ann" + //
											"where ann.entry IN ";
	private static final String	UNANNOTATED	= // + ( ... )!
											"from Annotation ann" + //
											"where ann.entry NOT IN ";

	@SuppressWarnings("unchecked")
	public List<Annotation> getRecent() {
		Criteria crit = getSession().createCriteria(Annotation.class);
		crit.addOrder(Order.desc("timestamp"));
		crit.setMaxResults(10);
		return crit.list();
	}
}
