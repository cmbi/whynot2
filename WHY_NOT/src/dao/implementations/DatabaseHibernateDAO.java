package dao.implementations;

import java.util.HashSet;
import java.util.Set;

import model.Database;
import model.EntryFile;

import org.hibernate.Query;

import dao.interfaces.DatabaseDAO;

public class DatabaseHibernateDAO extends GenericHibernateDAO<Database, String> implements DatabaseDAO {
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

	private static final String	ANNOTATED	= // + ( ... )!
											"from Annotation ann" + //
											"where ann.entry IN ";

	private static final String	UNANNOTATED	= // + ( ... )!
											"from Annotation ann" + //
											"where ann.entry NOT IN ";

	public int getMissingCount(Database db) {
		Query q = getSession().createQuery("select count(*) as count " + DatabaseHibernateDAO.MISSING);
		q.setParameter("child", db);
		q.setParameter("parent", db.getParent());
		return (Integer) q.uniqueResult();
	}

	public Set<EntryFile> getValidEntries(Database db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.VALID).setParameter("child", db);
		return new HashSet<EntryFile>(q.list());
	}

	public Set<EntryFile> getMissingEntries(Database db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.MISSING).setParameter("child", db).setParameter("parent", db.getParent());
		return new HashSet<EntryFile>(q.list());
	}

	public Set<EntryFile> getObsoleteEntries(Database db) {
		Query q = getSession().createQuery(DatabaseHibernateDAO.OBSOLETE).setParameter("child", db);
		return new HashSet<EntryFile>(q.list());
	}
}
