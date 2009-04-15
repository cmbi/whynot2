package dao.implementations;

import java.util.HashSet;
import java.util.Set;

import model.Databank;
import model.DBFile;

import org.hibernate.Query;

import dao.interfaces.DatabaseDAO;

public class DatabaseHibernateDAO extends GenericHibernateDAO<Databank, String> implements DatabaseDAO {
	private static final String	VALID		= //
											"from EntryFile par, EntryFile chi " + //
											"where chi.entry.database = :child and " + //
											"par.entry = ( chi.entry.database.parent, chi.entry.pdbid )";

	private static final String	MISSING		= //
											"from EntryFile par " + //
											"where par.entry.database = :parent and " + //
											"(select chi.path from EntryFile chi " + //
											"where chi.entry.database = :child and chi.entry.pdbid = par.entry.pdbid ) is null";

	private static final String	OBSOLETE	= //
											"from EntryFile chi " + //
											"where chi.entry.database = :child and " + //
											"(select par.path from EntryFile par " + //
											"where par.entry = ( chi.entry.database.parent, chi.entry.pdbid )) is null";

	public long getValidCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabaseHibernateDAO.VALID).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<DBFile> getValidEntries(Databank db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.VALID).setParameter("child", db);
		return new HashSet<DBFile>(q.list());
	}

	public long getMissingCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabaseHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<DBFile> getMissingEntries(Databank db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return new HashSet<DBFile>(q.list());
	}

	public long getObsoleteCount(Databank db) {
		Query q = getSession().createQuery("select count(*) " + DatabaseHibernateDAO.OBSOLETE).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Set<DBFile> getObsoleteEntries(Databank db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.OBSOLETE).setParameter("child", db);
		return new HashSet<DBFile>(q.list());
	}
}
