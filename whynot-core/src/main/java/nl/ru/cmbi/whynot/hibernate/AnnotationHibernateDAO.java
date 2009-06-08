package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Long> implements AnnotationDAO {
	public long countAllWith(Comment comment) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("comment", comment));
		crit.setProjection(Projections.rowCount());
		return (Integer) crit.uniqueResult();
	}

	public long getLatest(Comment comment) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("comment", comment));
		crit.setProjection(Projections.max("timestamp"));
		return (Long) crit.uniqueResult();
	}
}
