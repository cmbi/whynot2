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
import org.hibernate.criterion.Property;


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

    private Criteria createCriteria(final String alias, final Criterion... criterion) {
        Criteria crit = getSession().createCriteria(Databank.class, alias);
        for (Criterion c : criterion)
            crit.add(c);
        return crit;
    }

    public Databank getRoot() {
    
        Criteria c = createCriteria("db").createAlias("db.parent","parent")
            .add(Property.forName("db.name").eqProperty("parent.name"));

        return (Databank) c.uniqueResult();
    }

	@Transactional
	public List<Databank> findAll() {
		// Get root databank PDB
		Databank root = getRoot();

		// Get all databanks
		List<Databank> allDatabanks = createCriteria().addOrder(Order.asc("name")).list();

		// Return databanks in hierachical order
		return getDatabanksInTreeOrder(root, allDatabanks);
	}

	private List<Databank> getDatabanksInTreeOrder(final Databank rootdb, final List<Databank> allDatabanks) {
		List<Databank> children = new ArrayList<Databank>();
		// Add root node
		children.add(rootdb);
		for (Databank child : allDatabanks)
			if (!rootdb.equals(child) && child.getParent().equals(rootdb))
				// Recursively add child itself and grandchildren
				children.addAll(getDatabanksInTreeOrder(child, allDatabanks));
		return children;
	}
}
