package dao.implementations;

import java.util.SortedSet;
import java.util.TreeSet;

import model.Databank;
import model.File;

import org.hibernate.Query;

import dao.interfaces.DatabankDAO;

public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, String> implements DatabankDAO {
	private final String	VALID		= //
										"from File par, File chi " + //
										"where chi.databank = :child " + //
										"and par.databank = chi.databank.parent " + //
										"and par.pdbid = chi.pdbid ";

	private final String	MISSING		= //
										"from File par " + //
										"where par.databank = :parent " + //
										"and (select chi.path from File chi " + //
										"where chi.databank = :child and chi.pdbid = par.pdbid ) is null";

	private final String	OBSOLETE	= //
										"from File chi " + //
										"where chi.databank = :child " + //
										"and (select par.path from File par " + //
										"where par.databank = chi.databank.parent " + //
										"and par.pdbid = chi.pdbid ) is null";

	private final String	ANNCOUNT	= "select count(*) from Annotation ann where chi.pdbid=ann.entry.pdbid";

	private final String	WITH		= "and (" + ANNCOUNT + ") > 0 ";

	private final String	WITHOUT		= "and (" + ANNCOUNT + ") = 0 ";

	public long getValidCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + VALID).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<File> getValidEntries(Databank db) {
		Query q = getSession().createQuery("select chi " + VALID).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}

	@SuppressWarnings("unchecked")
	public SortedSet<File> getValidEntriesWith(Databank db) {
		Query q = getSession().createQuery("select chi " + VALID + WITH).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}

	@SuppressWarnings("unchecked")
	public SortedSet<File> getValidEntriesWithout(Databank db) {
		Query q = getSession().createQuery("select chi " + VALID + WITHOUT).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}

	public long getMissingCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<File> getMissingEntries(Databank db) {
		Query q = getSession().createQuery(MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return new TreeSet<File>(q.list());
	}

	public long getObsoleteCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + OBSOLETE).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public SortedSet<File> getObsoleteEntries(Databank db) {
		Query q = getSession().createQuery(OBSOLETE).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}
}
