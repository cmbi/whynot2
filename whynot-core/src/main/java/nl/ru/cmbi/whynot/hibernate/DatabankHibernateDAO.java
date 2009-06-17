package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, Long> implements DatabankDAO {
	@Override
	@SuppressWarnings("unchecked")
	public List<Databank> getAll() {
		return createCriteria().addOrder(Order.asc("name")).list();
	}

	public Databank findByName(String name) {
		return (Databank) createCriteria(Restrictions.naturalId().set("name", name)).uniqueResult();
	}
}
