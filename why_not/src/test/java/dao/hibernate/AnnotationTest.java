package dao.hibernate;

import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import dao.interfaces.AnnotationDAO;
import dao.interfaces.CommentDAO;
import dao.interfaces.DatabankDAO;
import dao.interfaces.EntryDAO;

public class AnnotationTest extends DAOTest {
	@Test
	public void storeAnnotation() throws Exception {
		transaction = factory.getSession().beginTransaction();

		AnnotationDAO anndao = factory.getAnnotationDAO();
		CommentDAO comdao = factory.getCommentDAO();
		DatabankDAO dbdao = factory.getDatabankDAO();
		EntryDAO entdao = factory.getEntryDAO();

		Comment comment = new Comment("Dit is mijn comment");
		Comment strdCom = comdao.findByNaturalId(Restrictions.naturalId().set("text", comment.getText()));
		if (strdCom != null)
			comment = strdCom;
		else
			comdao.makePersistent(comment);

		Databank db = dbdao.findByNaturalId(Restrictions.naturalId().set("name", "PDB"));
		if (db == null)
			throw new Exception("DB NOT FOUND");

		Entry entry = new Entry(db, "xTim");
		Entry strdEnt = entdao.findByNaturalId(Restrictions.naturalId().set("databank", db).set("pdbid", entry.getPdbid()));
		if (strdEnt != null)
			entry = strdEnt;
		else
			entdao.makePersistent(entry);

		Annotation ann = new Annotation(comment, entry);
		Annotation strdAnn = anndao.findByNaturalId(Restrictions.naturalId().set("comment", comment).set("entry", entry));
		if (strdAnn == null)
			anndao.makePersistent(ann);

		transaction.commit();
	}

}
