package dao.implementations;

import java.util.HashSet;
import java.util.Set;


import old_model.Databank;
import old_model.Entry;

import org.hibernate.Query;

import dao.interfaces.DatabankDAO;

public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, String> implements DatabankDAO {
	private static final String	VALID		= //
											"from DBFile par, DBFile chi " + //
											"where chi.databank = :child and " + //
											"par.entry = ( chi.databank.parent, chi.pdbid )";

	private static final String	MISSING		= //
											"from DBFile par " + //
											"where par.databank = :parent and " + //
											"(select chi.path from DBFile chi " + //
											"where chi.databank = :child and chi.pdbid = par.pdbid ) is null";

	private static final String	OBSOLETE	= //
											"from DBFile chi " + //
											"where chi.databank = :child and " + //
											"(select par.path from DBFile par " + //
											"where par.entry = ( chi.databank.parent, chi.pdbid )) is null";

	public long getValidCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.VALID).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<Entry> getValidEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.VALID).setParameter("child", db);
		return new HashSet<Entry>(q.list());
	}

	public long getMissingCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<Entry> getMissingEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return new HashSet<Entry>(q.list());
	}

	public long getObsoleteCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.OBSOLETE).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<Entry> getObsoleteEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.OBSOLETE).setParameter("child", db);
		return new HashSet<Entry>(q.list());
	}
}
