package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.FileRepoCustom;
import nl.ru.cmbi.whynot.model.File;
import org.hibernate.criterion.Restrictions;

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
	public List<File> getRecent() {
		return createCriteria().addOrder(Order.desc("timestamp")).setMaxResults(10).list();
	}

	@Override
	public File findFile( java.io.File file ) {

		return (File) createCriteria(Restrictions.eq("path", file.getPath()), Restrictions.eq("timestamp", file.lastModified())).uniqueResult();
	}
}
