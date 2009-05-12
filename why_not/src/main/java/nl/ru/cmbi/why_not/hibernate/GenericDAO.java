package nl.ru.cmbi.why_not.hibernate;

import java.io.Serializable;
import java.util.List;

import nl.ru.cmbi.why_not.model.Annotation;
import nl.ru.cmbi.why_not.model.Comment;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;
import nl.ru.cmbi.why_not.model.File;

import org.hibernate.Session;
import org.hibernate.criterion.NaturalIdentifier;

public interface GenericDAO<T, ID extends Serializable> {
	long count();

	T findById(ID id, boolean lock);

	T findByNaturalId(NaturalIdentifier id);

	List<T> findAll();

	List<T> findByExample(T exampleInstance, String[] excludeProperty);

	T makePersistent(T entity);

	void makeTransient(T entity);

	Session getSession();

	public interface AnnotationDAO extends GenericDAO<Annotation, Long> {
	}

	public interface CommentDAO extends GenericDAO<Comment, Long> {
	}

	public interface DatabankDAO extends GenericDAO<Databank, Long> {
	}

	public interface EntryDAO extends GenericDAO<Entry, Long> {
	}

	public interface FileDAO extends GenericDAO<File, Long> {
	}
}
