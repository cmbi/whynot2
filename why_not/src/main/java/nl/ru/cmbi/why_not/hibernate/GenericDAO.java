package nl.ru.cmbi.why_not.hibernate;

import java.io.Serializable;
import java.util.List;

import nl.ru.cmbi.why_not.model.Annotation;
import nl.ru.cmbi.why_not.model.Comment;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;
import nl.ru.cmbi.why_not.model.File;

import org.hibernate.criterion.NaturalIdentifier;
import org.springframework.transaction.annotation.Transactional;

public interface GenericDAO<T, ID extends Serializable> {
	Long countAll();

	//Finders
	T findById(ID id, boolean lock);

	@Deprecated
	T findByNaturalId(NaturalIdentifier id);

	List<T> findAll();

	List<T> findByExample(T exampleInstance, String... excludeProperty);

	T findOrCreateByExample(T exampleInstance, String... excludeProperty);

	//Save / Delete
	@Transactional
	T makePersistent(T entity);

	@Transactional
	void makeTransient(T entity);

	//Filter
	void enableFilter(String filterName, String... params);

	void disableFilter(String filterName);

	//Interfaces
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
