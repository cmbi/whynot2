package nl.ru.cmbi.whynot.hibernate;

import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabankHibernateDAO extends GenericHibernateDAO<Databank> implements DatabankDAO {
	@Override
	public Databank findByName(final String name) {
		return (Databank) createCriteria(Restrictions.naturalId().set("name", name)).uniqueResult();
	}

	@Override
	@Transactional
	public List<Databank> getAll() {
		// Get root databank PDB
		Databank pdb = findByName("PDB");

		// Get all databanks
		@SuppressWarnings("unchecked")
		List<Databank> allDatabanks = createCriteria().addOrder(Order.asc("name")).list();

		// Return databanks in hierachical order
		return getDatabanksInTreeOrder(pdb, allDatabanks);
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
