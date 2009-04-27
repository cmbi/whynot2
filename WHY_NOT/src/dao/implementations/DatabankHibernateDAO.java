package dao.implementations;

import java.util.Set;
import java.util.TreeSet;

import model.Databank;
import model.File;

import org.hibernate.Query;

import dao.interfaces.DatabankDAO;

public class DatabankHibernateDAO extends GenericHibernateDAO<Databank, String> implements DatabankDAO {
	private final String	ALL			= //
										"from File target " + //
										"where target.databank = :child";

	private final String	VALID		= //
										"from File par, File target " + //
										"where target.databank = :child " + //
										"and par.databank = target.databank.parent " + //
										"and par.pdbid = target.pdbid ";

	private final String	MISSING		= //
										"from File target " + //
										"where target.databank = :parent " + //
										"and (select chi.path from File chi " + //
										"where chi.databank = :child and chi.pdbid = target.pdbid ) is null";

	private final String	OBSOLETE	= //
										"from File target " + //
										"where target.databank = :child " + //
										"and (select par.path from File par " + //
										"where par.databank = target.databank.parent " + //
										"and par.pdbid = target.pdbid ) is null";

	private final String	ANNCOUNT	= "select count(*) from Annotation ann where target.pdbid = ann.entry.pdbid ";

	private String selectAnnotationType(AnnotationType at) {
		switch (at) {
		case ALL:
			return "";
		case WITH:
			return "and (" + ANNCOUNT + ") > 0 ";
		case WITHOUT:
			return "and (" + ANNCOUNT + ") = 0 ";
		}
		return null;
	}

	@Override
	public long getCount(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select count(*) " + ALL + selectAnnotationType(at)).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@Override
	public long getValidCount(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select count(*) " + VALID + selectAnnotationType(at)).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@Override
	public long getMissingCount(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select count(*) " + MISSING + selectAnnotationType(at)).setParameter("child", db).setParameter("parent", db.getParent());
		return (Long) q.uniqueResult();
	}

	@Override
	public long getObsoleteCount(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select count(*) " + OBSOLETE + selectAnnotationType(at)).setParameter("child", db);
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<File> getEntries(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select target " + ALL + selectAnnotationType(at)).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<File> getValidEntries(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select target " + VALID + selectAnnotationType(at)).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<File> getMissingEntries(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select target " + MISSING + selectAnnotationType(at)).setParameter("child", db).setParameter("parent", db.getParent());
		return new TreeSet<File>(q.list());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<File> getObsoleteEntries(AnnotationType at, Databank db) {
		Query q = getSession().createQuery("select target " + OBSOLETE + selectAnnotationType(at)).setParameter("child", db);
		return new TreeSet<File>(q.list());
	}
}
