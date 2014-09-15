package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Service;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.EntryRepoCustom;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

@Service
public class EntryRepoImpl implements EntryRepoCustom {
	private static final Class<Entry>	persistentClass	= Entry.class;

	@PersistenceContext
	private EntityManager				entityManager;

	private Session getSession() {
		return (Session) entityManager.getDelegate();
	}

	private Criteria createCriteria(final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(persistentClass);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	private Criteria createCriteria(final String alias, final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(persistentClass, alias);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	@Override
	public boolean contains(final String pdbid) {
		Criteria crit = createCriteria(Restrictions.naturalId().set("pdbid", pdbid));
		crit.setProjection(Projections.rowCount());
		return 0 < (Long) crit.uniqueResult();
	}

	// Present
	private Criteria presentCriteria(final Databank db) {
		return createCriteria(Restrictions.eq("databank", db), Restrictions.isNotNull("file"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getPresent(final Databank db) {
		return presentCriteria(db).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long countPresent(final Databank db) {
		return (Long) presentCriteria(db).setProjection(Projections.rowCount()).uniqueResult();
	}

	// Valid
	private Criteria validCriteria(final Databank db) {
		// Start out with present entries
		Criteria query = createCriteria("child");
		query.add(Restrictions.eq("databank", db));
		query.add(Restrictions.isNotNull("file"));

		// Correlated subquery to retain those that have a present parent entry
		DetachedCriteria sub = DetachedCriteria.forClass(persistentClass, "parent");
		sub.add(Restrictions.eq("databank", db.getParent()));
		sub.add(Restrictions.isNotNull("file"));
		sub.add(Property.forName("child.pdbid").eqProperty("parent.pdbid"));

		// Subqueries must have a projection set
		sub.setProjection(Projections.id());
		query.add(Subqueries.exists(sub));
		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getValid(final Databank db) {
		return validCriteria(db).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long countValid(final Databank db) {
		Criteria query = validCriteria(db);

		// Return rowcount
		query.setProjection(Projections.rowCount());
		return (Long) query.uniqueResult();
	}

	// Obsolete
	private Criteria obsoleteCriteria(final Databank db) {
		// Start out with present entries
		Criteria query = createCriteria("child");
		query.add(Restrictions.eq("databank", db));
		query.add(Restrictions.isNotNull("file"));

		// Correlated subquery to retain those that do not have a present parent entry
		DetachedCriteria sub = DetachedCriteria.forClass(persistentClass, "parent");
		sub.add(Restrictions.eq("databank", db.getParent()));
		sub.add(Restrictions.isNotNull("file"));
		sub.add(Property.forName("child.pdbid").eqProperty("parent.pdbid"));

		// Subqueries must have a projection set
		sub.setProjection(Projections.id());
		query.add(Subqueries.notExists(sub));
		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getObsolete(final Databank db) {
		return obsoleteCriteria(db).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long countObsolete(final Databank db) {
		return (Long) obsoleteCriteria(db).setProjection(Projections.rowCount()).uniqueResult();
	}

	// Annotated
	private Criteria annotatedCriteria(final Databank db) {
		// Start out with present entries
		Criteria query = createCriteria("child");
		query.add(Restrictions.eq("databank", db));
		query.add(Restrictions.isNull("file"));
		query.add(Restrictions.isNotEmpty("annotations"));

		// Correlated subquery to retain those that have a present parent entry
		DetachedCriteria sub = DetachedCriteria.forClass(persistentClass, "parent");
		sub.add(Restrictions.eq("databank", db.getParent()));
		sub.add(Restrictions.isNotNull("file"));
		sub.add(Property.forName("child.pdbid").eqProperty("parent.pdbid"));

		// Subqueries must have a projection set
		sub.setProjection(Projections.id());
		query.add(Subqueries.exists(sub));
		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getAnnotated(final Databank db) {
		return annotatedCriteria(db).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long countAnnotated(final Databank db) {
		return (Long) annotatedCriteria(db).setProjection(Projections.rowCount()).uniqueResult();
	}

	// Missing
	private Criteria missingCriteria(final Databank child) {
		// Start by selecting all existing parent entries
		Criteria query = createCriteria("parent");
		query.add(Restrictions.eq("databank", child.getParent()));
		query.add(Restrictions.isNotNull("file"));

		// Filter out those that have an existing child
		DetachedCriteria sub = DetachedCriteria.forClass(persistentClass, "child");
		sub.add(Restrictions.eq("databank", child));
		sub.add(Restrictions.isNotNull("file"));
		sub.add(Property.forName("child.pdbid").eqProperty("parent.pdbid"));

		// Subqueries must have a projection set
		sub.setProjection(Projections.id());
		query.add(Subqueries.notExists(sub));

		// FIXME Obsolete parent entries should (maybe) not result in missing child entries
		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getMissing(final Databank child) {
		return missingCriteria(child).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long countMissing(final Databank child) {
		return (Long) missingCriteria(child).setProjection(Projections.rowCount()).uniqueResult();
	}

	// Unannotated
	private Criteria unannotatedCriteria(final Databank child) {
		// Start by selecting all existing parent entries
		Criteria query = createCriteria("parent");
		query.add(Restrictions.eq("databank", child.getParent()));
		query.add(Restrictions.isNotNull("file"));

		// Filter out those that have childs with either a file or annotation
		DetachedCriteria sub = DetachedCriteria.forClass(persistentClass, "child");
		sub.add(Property.forName("child.pdbid").eqProperty("parent.pdbid"));
		sub.add(Restrictions.eq("databank", child));
		sub.add(Restrictions.or(Restrictions.isNotNull("file"), Restrictions.isNotEmpty("annotations")));

		// Subqueries must have a projection set
		sub.setProjection(Projections.id());
		query.add(Subqueries.notExists(sub));
		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getUnannotated(final Databank child) {
		return unannotatedCriteria(child).addOrder(Order.asc("pdbid")).list();
	}

	@Override
	public long counUnannotated(final Databank child) {
		return (Long) unannotatedCriteria(child).setProjection(Projections.rowCount()).uniqueResult();
	}
}
