package nl.ru.cmbi.whynot.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericHibernateDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
	private Class<T>		persistentClass;

	@Autowired
	private SessionFactory	sessionFactory;

	@SuppressWarnings("unchecked")
	public GenericHibernateDAO() {
		persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public long countAll() {
		return (Integer) createCriteria().setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return createCriteria().list();
	}

	/**
	 * Use this inside subclasses as a convenience method.
	 */
	protected Criteria createCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(persistentClass);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	//Save / Delete
	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	public void makeTransient(T entity) {
		getSession().delete(entity);
	}
}
