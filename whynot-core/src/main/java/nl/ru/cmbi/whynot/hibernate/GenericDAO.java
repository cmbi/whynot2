package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.DomainObject;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.springframework.transaction.annotation.Transactional;

/**
 * Data Access Object interfaces. Naming convention: Find - single Get - multiple
 * 
 * @param <T>
 *            Type of DomainObject accessible from implementing DAO.
 */
public interface GenericDAO<T extends DomainObject> {
	long countAll();

	T find(final Long id);

	List<T> getAll();

	// Save / Delete
	@Transactional
	void makePersistent(final T entity);

	@Transactional
	void makeTransient(final T entity);

	// Interfaces
	public interface AnnotationDAO extends GenericDAO<Annotation> {
		long getLastUsed(final Comment comment);

		List<Annotation> getRecent();

		long countWith(final Comment comment);

		List<Entry> getEntriesForComment(final Long l);
	}

	public interface CommentDAO extends GenericDAO<Comment> {
		Comment findByText(final String text);
	}

	public interface DatabankDAO extends GenericDAO<Databank> {
		Databank findByName(final String name);
	}

	public interface EntryDAO extends GenericDAO<Entry> {
		Entry findByDatabankAndPdbid(final Databank databank, final String pdbid);

		boolean contains(final String pdbid);

		// Collections
		long countPresent(final Databank db);

		long countValid(final Databank db);

		long countObsolete(final Databank db);

		long countMissing(final Databank db);

		long countAnnotated(final Databank db);

		long counUnannotated(final Databank db);

		List<Entry> getPresent(final Databank db);

		List<Entry> getValid(final Databank db);

		List<Entry> getObsolete(final Databank db);

		/**
		 * @param db
		 * @return parents!
		 */
		List<Entry> getMissing(final Databank db);

		List<Entry> getAnnotated(final Databank db);

		/**
		 * @param db
		 * @return parents!
		 */
		List<Entry> getUnannotated(final Databank db);
	}

	public interface FileDAO extends GenericDAO<File> {
		File findByPathAndTimestamp(final String path, final Long timestamp);

		List<File> getRecent();
	}
}
