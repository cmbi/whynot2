package nl.ru.cmbi.whynot.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.model.Databank;

@Service
public class DatabankRepoImpl {
	@PersistenceContext
	private EntityManager	entityManager;

	private Session getSession() {
		return (Session) entityManager.getDelegate();
	}


	private Criteria createCriteria(final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(Databank.class);
		for (Criterion c : criterion)
			crit.add(c);
		return crit;
	}

	@Transactional
	public List<Databank> findAll() {
		// Get root databank PDB
		Databank pdb = (Databank) createCriteria(Restrictions.eq("name", "PDB")).uniqueResult();

		// Get all databanks
		List<Databank> allDatabanks = createCriteria().addOrder(Order.asc("name")).list();

		// Return databanks in hierachical order
		return getDatabanksInTreeOrder(pdb, allDatabanks);
	}

	private List<Databank> getDatabanksInTreeOrder(final Databank rootdb, final List<Databank> allDatabanks) {
		List<Databank> children = new ArrayList<>();
		// Add root node
		children.add(rootdb);
		for (Databank child : allDatabanks)
			if (!rootdb.equals(child) && child.getParent().equals(rootdb))
				// Recursively add child itself and grandchildren
				children.addAll(getDatabanksInTreeOrder(child, allDatabanks));
		return children;
	}
}
