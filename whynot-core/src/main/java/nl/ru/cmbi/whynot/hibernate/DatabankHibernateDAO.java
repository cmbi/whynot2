package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, Long> implements DatabankDAO {
	@Override
	@SuppressWarnings("unchecked")
	public List<Databank> findAll() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		return crit.addOrder(Order.asc("name")).list();
	}

	public Databank findByName(String name) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("name", name));
		return (Databank) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Databank> getChildren(Databank parent) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("parent", parent));
		crit.add(Restrictions.ne("name", parent.getName()));
		crit.addOrder(Order.asc("name"));
		return crit.list();
	}
}
