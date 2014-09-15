package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.FileRepoCustom;
import nl.ru.cmbi.whynot.model.File;

@Service
public class FileRepoImpl implements FileRepoCustom {
	@PersistenceContext
	private EntityManager	entityManager;

	private Session getSession() {
		return (Session) entityManager.getDelegate();
	}

	private Criteria createCriteria(final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(File.class);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	@Override
	public File findByPathAndTimestamp(final String path, final Long timestamp) {
		return (File) createCriteria(Restrictions.naturalId().set("path", path).set("timestamp", timestamp)).uniqueResult();
	}

	@Override
	public List<File> getRecent() {
		return createCriteria().addOrder(Order.desc("timestamp")).setMaxResults(10).list();
	}
}
