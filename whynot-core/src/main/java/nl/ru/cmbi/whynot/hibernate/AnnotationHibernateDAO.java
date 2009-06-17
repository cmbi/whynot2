package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class AnnotationHibernateDAO extends GenericHibernateDAO<Annotation, Long> implements AnnotationDAO {
	public long getLastUsed(Comment comment) {
		Criteria crit = createCriteria(Restrictions.naturalId().set("comment", comment));
		return (Long) crit.setProjection(Projections.max("timestamp")).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Annotation> getRecent() {
		return createCriteria().addOrder(Order.desc("timestamp")).setMaxResults(10).list();
	}

	public int countWith(Comment comment) {
		Criteria crit = createCriteria(Restrictions.naturalId().set("comment", comment));
		return (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Entry> getEntriesForComment(Long comment) {
		Query q = getSession().createQuery("select entry from Annotation where comment_id = ?").setLong(0, comment);
		return q.list();
	}
}
