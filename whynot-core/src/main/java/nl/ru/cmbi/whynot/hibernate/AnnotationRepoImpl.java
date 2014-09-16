package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.AnnotationRepoCustom;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

@Service
public class AnnotationRepoImpl implements AnnotationRepoCustom {
	@PersistenceContext
	private EntityManager	entityManager;

	@Override
	public Annotation findByCommentAndEntry(final Comment comment, final Entry entry) {
		Session session = (Session) entityManager.getDelegate();
		return (Annotation) session
				.byNaturalId(Annotation.class)
				.using("entry", entry)
				.using("comment", comment)
				.getReference();
	}

	private Session getSession() {
		return (Session) entityManager.getDelegate();
	}

	private Criteria createCriteria(final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(Annotation.class);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	@Override
	public long getLastUsed(final Comment comment) {
		Criteria crit = createCriteria(Restrictions.naturalId().set("comment", comment));
		return (Long) crit.setProjection(Projections.max("timestamp")).uniqueResult();
	}

	@Override
	public List<Annotation> getRecent() {
		return createCriteria().addOrder(Order.desc("timestamp")).setMaxResults(10).list();
	}

	@Override
	public long countWith(final Comment comment) {
		Criteria crit = createCriteria(Restrictions.naturalId().set("comment", comment));
		return (Long) crit.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	public List<Entry> getEntriesForComment(final Long comment) {
		Query q = getSession().createQuery("select entry from Annotation where comment_id = ?").setLong(0, comment);
		return q.list();
	}
}
