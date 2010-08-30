package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

@Service
public class DatabankHibernateDAO extends GenericHibernateDAO<Databank> implements DatabankDAO {
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Databank> getAll() {
		return createCriteria().addOrder(Order.asc("name")).list();
	}

	@Override
	public Databank findByName(String name) {
		return (Databank) createCriteria(Restrictions.naturalId().set("name", name)).uniqueResult();
	}
}
