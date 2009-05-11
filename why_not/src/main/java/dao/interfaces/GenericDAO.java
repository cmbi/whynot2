package dao.interfaces;

import java.io.Serializable;
import java.util.List;

import model.Annotation;
import model.Comment;
import model.Databank;
import model.Entry;
import model.File;

import org.hibernate.criterion.NaturalIdentifier;

public interface GenericDAO<T, ID extends Serializable> {
	long count();

	T findById(ID id, boolean lock);

	T findByNaturalId(NaturalIdentifier id);

	List<T> findAll();

	List<T> findByExample(T exampleInstance, String[] excludeProperty);

	T makePersistent(T entity);

	void makeTransient(T entity);

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
