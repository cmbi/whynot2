package nl.ru.cmbi.whynot.hibernate;

import java.io.Serializable;
import java.util.List;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.springframework.transaction.annotation.Transactional;

/**
 * Count - count
 * Find - single
 * Get - multiple
 */
public interface GenericDAO<T, ID extends Serializable> {
	long countAll();

	List<T> getAll();

	//Save / Delete
	@Transactional
	T makePersistent(T entity);

	@Transactional
	void makeTransient(T entity);

	//Interfaces
	public interface AnnotationDAO extends GenericDAO<Annotation, Long> {
		long getLastUsed(Comment comment);

		List<Annotation> getRecent();

		int countWith(Comment comment);

		List<Entry> getEntriesForComment(Long l);
	}

	public interface CommentDAO extends GenericDAO<Comment, Long> {
		Comment findByText(String text);
	}

	public interface DatabankDAO extends GenericDAO<Databank, Long> {
		Databank findByName(String name);
	}

	public interface EntryDAO extends GenericDAO<Entry, Long> {
		Entry findByDatabankAndPdbid(Databank databank, String pdbid);

		boolean contains(String pdbid);

		//Collections
		int countPresent(Databank db);

		int countValid(Databank db);

		int countObsolete(Databank db);

		int countMissing(Databank db);

		int countAnnotated(Databank db);

		int counUnannotated(Databank db);

		List<Entry> getPresent(Databank db);

		List<Entry> getValid(Databank db);

		List<Entry> getObsolete(Databank db);

		/**
		 * @param db
		 * @return parents!
		 */
		List<Entry> getMissing(Databank db);

		List<Entry> getAnnotated(Databank db);

		/**
		 * @param db
		 * @return parents!
		 */
		List<Entry> getUnannotated(Databank db);
	}

	public interface FileDAO extends GenericDAO<File, Long> {
		File findByPathAndTimestamp(String path, Long timestamp);

		List<File> getRecent();
	}
}
