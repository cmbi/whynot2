package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class EntryHibernateDAO extends GenericHibernateDAO<Entry, Long> implements EntryDAO {
	public Entry findByDatabankAndPdbid(Databank databank, String pdbid) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("databank", databank).set("pdbid", pdbid));
		return (Entry) crit.uniqueResult();
	}

	//TODO Rewrite some of these queries to projections

	@SuppressWarnings("unchecked")
	public List<Entry> getValid(Databank child) {//Child file present, parent file present
		Query q = getSession().createQuery("from Entry child where file is not null and child.databank = :child_db and (select parent.file from Entry parent where parent.pdbid = child.pdbid and parent.databank = :parent_db) is not null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return q.list();
	}

	public long getValidCount(Databank child) {//Child file present, parent file present
		Query q = getSession().createQuery("select count(*) from Entry child where file is not null and child.databank = :child_db and (select parent.file from Entry parent where parent.pdbid = child.pdbid and parent.databank = :parent_db) is not null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Entry> getObsolete(Databank child) {//Child file present, no parent file
		Query q = getSession().createQuery("from Entry child where file is not null and child.databank = :child_db and (select parent.file from Entry parent where parent.pdbid = child.pdbid and parent.databank = :parent_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return q.list();
	}

	public long getObsoleteCount(Databank child) {//Child file present, no parent file
		Query q = getSession().createQuery("select count(*) from Entry child where file is not null and child.databank = :child_db and (select parent.file from Entry parent where parent.pdbid = child.pdbid and parent.databank = :parent_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Entry> getMissing(Databank child) {//Parent file present, no child file
		Query q = getSession().createQuery("from Entry parent where file is not null and parent.databank = :parent_db and (select child.file from Entry child where parent.pdbid = child.pdbid and child.databank = :child_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return q.list();
	}

	public long getMissingCount(Databank child) {//Parent file present, no child file
		Query q = getSession().createQuery("select count(*) from Entry parent where file is not null and parent.databank = :parent_db and (select child.file from Entry child where parent.pdbid = child.pdbid and child.databank = :child_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Entry> getUnannotated(Databank child) {//Parent file present, no child (because no file & no annotation => no child)
		Query q = getSession().createQuery("from Entry parent where file is not null and parent.databank = :parent_db and (select child from Entry child where parent.pdbid = child.pdbid and child.databank = :child_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return q.list();
	}

	public long getUnannotatedCount(Databank child) {//Parent file present, no child (because no file & no annotation => no child)
		Query q = getSession().createQuery("select count(*) from Entry parent where file is not null and parent.databank = :parent_db and (select child from Entry child where parent.pdbid = child.pdbid and child.databank = :child_db) is null");
		q.setParameter("child_db", child);
		q.setParameter("parent_db", child.getParent());
		return (Long) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Entry> getAnnotated(Databank child) {//No child file (annotations implicitly present or there wouldnt be a child)
		Query q = getSession().createQuery("from Entry child where child.file is null and child.databank = :child_db");
		q.setParameter("child_db", child);
		return q.list();
	}

	public long getAnnotatedCount(Databank child) {//No child file (annotations implicitly present or there wouldnt be a child)
		Query q = getSession().createQuery("select count(*) from Entry child where child.file is null and child.databank = :child_db");
		q.setParameter("child_db", child);
		return (Long) q.uniqueResult();
	}

	public void cleanUp() {
		int count = getSession().createSQLQuery(// 
		"delete from entry where id in " + //
		"(select e.id from entry e " + // 
		"left outer join annotation a on e.id=a.entry_id " + //
		"where a.entry_id is null and e.file_id is null)").executeUpdate();
		Logger.getLogger(getClass()).info("Removed " + count + " unused entries");

		//TODO: Also delete entries without both file and parent file
	}
}
