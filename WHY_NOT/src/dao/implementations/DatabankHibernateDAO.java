package dao.implementations;

import java.util.SortedSet;
import java.util.TreeSet;

import model.Databank;
import model.Entry;

import org.hibernate.Query;

import dao.interfaces.DatabankDAO;

public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, String> implements DatabankDAO {
	private static final String	VALID		= //
											"from File par, File chi " + //
											"where chi.databank = :child " + //
											"and par.databank = chi.databank.parent " + //
											"and par.pdbid = chi.pdbid ";

	private static final String	MISSING		= //
											"from File par " + //
											"where par.databank = :parent and " + //
											"(select chi.path from File chi " + //
											"where chi.databank = :child and chi.pdbid = par.pdbid ) is null";

	private static final String	OBSOLETE	= //
											"from File chi " + //
											"where chi.databank = :child and " + //
											"(select par.path from File par " + //
											"where par.databank = chi.databank.parent " + //
											"and par.pdbid = chi.pdbid ) is null";

	public long getValidCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.VALID).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<Entry> getValidEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.VALID).setParameter("child", db);
		return new TreeSet<Entry>(q.list());
	}

	public long getMissingCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<Entry> getMissingEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return new TreeSet<Entry>(q.list());
	}

	public long getObsoleteCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabankHibernateDAO.OBSOLETE).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<Entry> getObsoleteEntries(Databank db) {
		Query q = getSession().createQuery(DatabankHibernateDAO.OBSOLETE).setParameter("child", db);
		return new TreeSet<Entry>(q.list());
	}
}
