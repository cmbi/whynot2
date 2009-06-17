package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.File;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class FileHibernateDAO extends GenericHibernateDAO<File, Long> implements FileDAO {
	public File findByPathAndTimestamp(String path, Long timestamp) {
		return (File) createCriteria(Restrictions.naturalId().set("path", path).set("timestamp", timestamp)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<File> getRecent() {
		return createCriteria().addOrder(Order.desc("timestamp")).setMaxResults(10).list();
	}
}
