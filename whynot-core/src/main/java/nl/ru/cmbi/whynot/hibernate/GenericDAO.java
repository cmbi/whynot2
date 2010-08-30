package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.DomainObject;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

/**
 * Data Access Object interfaces.
 * Naming convention:
 * Find - single
 * Get - multiple
 * 
 * @param <T>
 *            Type of DomainObject accessible from implementing DAO.
 */
public interface GenericDAO<T extends DomainObject> {
	long countAll();

	T find(Long id);

	List<T> getAll();

	//Save / Delete
	@Transactional
	void makePersistent(T entity);

	@Transactional
	void makeTransient(T entity);

	//Interfaces
	public interface AnnotationDAO extends GenericDAO<Annotation> {
		long getLastUsed(Comment comment);

		List<Annotation> getRecent();

		int countWith(Comment comment);

		List<Entry> getEntriesForComment(Long l);
	}

	public interface CommentDAO extends GenericDAO<Comment> {
		Comment findByText(String text);
	}

	public interface DatabankDAO extends GenericDAO<Databank> {
		Databank findByName(String name);
	}

	public interface EntryDAO extends GenericDAO<Entry> {
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

	public interface FileDAO extends GenericDAO<File> {
		File findByPathAndTimestamp(String path, Long timestamp);

		List<File> getRecent();
	}
}
