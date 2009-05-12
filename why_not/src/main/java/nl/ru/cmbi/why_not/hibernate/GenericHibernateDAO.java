package nl.ru.cmbi.why_not.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.NaturalIdentifier;

public class GenericHibernateDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
	private Class<T>		persistentClass;
	private SessionFactory	sessionFactory;

	@SuppressWarnings("unchecked")
	public GenericHibernateDAO() {
		persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public GenericHibernateDAO(Class<T> type) {
		persistentClass = type;
	}

	public Session getSession() {
		if (sessionFactory == null)
			throw new IllegalStateException("SessionFactory has not been set on DAO before usage");
		return sessionFactory.getCurrentSession();
	}

	public void setSessionFactory(SessionFactory s) {
		sessionFactory = s;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public long count() {
		return (Long) getSession().createQuery("select count(*) from " + persistentClass.getName()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock)
			entity = (T) getSession().get(getPersistentClass(), id, LockMode.UPGRADE);
		else
			entity = (T) getSession().get(getPersistentClass(), id);

		return entity;
	}

	@SuppressWarnings("unchecked")
	public T findByNaturalId(NaturalIdentifier id) {
		Criteria crit = getSession().createCriteria(getPersistentClass()).add(id);
		return (T) crit.uniqueResult();
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty)
			example.excludeProperty(exclude);
		crit.add(example);
		return crit.list();
	}

	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	public void makeTransient(T entity) {
		getSession().delete(entity);
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	/**
	* Use this inside subclasses as a convenience method.
	*/
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		for (Criterion c : criterion)
			crit.add(c);
		return crit.list();
	}

}
