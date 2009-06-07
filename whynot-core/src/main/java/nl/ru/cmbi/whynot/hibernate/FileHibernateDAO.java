package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.FileDAO;
import nl.ru.cmbi.whynot.model.File;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class FileHibernateDAO extends GenericHibernateDAO<File, Long> implements FileDAO {
	public File findByPathAndTimestamp(String path, Long timestamp) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("path", path).set("timestamp", timestamp));
		return (File) crit.uniqueResult();
	}
}
