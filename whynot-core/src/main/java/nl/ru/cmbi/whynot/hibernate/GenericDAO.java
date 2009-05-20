package nl.ru.cmbi.whynot.hibernate;

import java.io.Serializable;
import java.util.List;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.springframework.transaction.annotation.Transactional;

public interface GenericDAO<T, ID extends Serializable> {
	Long countAll();

	//Finders
	T findById(ID id, boolean lock);

	List<T> findAll();

	T findByExample(T exampleInstance, String... excludeProperty);

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
		Comment findByText(String text);

		@Transactional
		void cleanUp();
	}

	public interface DatabankDAO extends GenericDAO<Databank, Long> {
		Databank findByName(String name);
	}

	public interface EntryDAO extends GenericDAO<Entry, Long> {
		Entry findByDatabankAndPdbid(Databank databank, String pdbid);

		@Transactional
		void cleanUp();
	}

	public interface FileDAO extends GenericDAO<File, Long> {
	}
}
