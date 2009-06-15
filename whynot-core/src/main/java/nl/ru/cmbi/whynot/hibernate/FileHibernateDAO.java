package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.File;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class FileHibernateDAO extends GenericHibernateDAO<File, Long> implements FileDAO {
	public File findByPathAndTimestamp(String path, Long timestamp) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("path", path).set("timestamp", timestamp));
		return (File) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<File> getRecent() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.addOrder(Order.desc("timestamp"));
		crit.setMaxResults(10);
		return crit.list();
	}
}
