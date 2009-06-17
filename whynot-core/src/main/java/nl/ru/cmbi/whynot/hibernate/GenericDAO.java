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
	long countAll();

	//Finders
	T findById(ID id, boolean lock);

	List<T> findAll();

	T findByExample(T exampleInstance, String... excludeProperty);

	//Save / Delete
	@Transactional
	T makePersistent(T entity);

	@Transactional
	void makeTransient(T entity);

	//Interfaces
	public interface AnnotationDAO extends GenericDAO<Annotation, Long> {
		long countAllWith(Comment comment);

		long getLastUsed(Comment comment);

		List<Annotation> getRecent();
	}

	public interface CommentDAO extends GenericDAO<Comment, Long> {
		Comment findByText(String text);
	}

	public interface DatabankDAO extends GenericDAO<Databank, Long> {
		Databank findByName(String name);

		List<Databank> getChildren(Databank parent);
	}

	public interface EntryDAO extends GenericDAO<Entry, Long> {
		Entry findByDatabankAndPdbid(Databank databank, String pdbid);

		boolean contains(String pdbid);

		@Transactional
		int removeEntriesWithoutBothFileAndParentFile();

		//Collections
		List<Entry> getPresent(Databank db);

		List<Entry> getValid(Databank db);

		List<Entry> getObsolete(Databank db);

		List<Entry> getMissing(Databank db);

		List<Entry> getAnnotated(Databank db);

		List<Entry> getUnannotated(Databank db);

		int getPresentCount(Databank db);

		long getValidCount(Databank db);

		long getObsoleteCount(Databank db);

		long getMissingCount(Databank db);

		long getAnnotatedCount(Databank db);

		long getUnannotatedCount(Databank db);
	}

	public interface FileDAO extends GenericDAO<File, Long> {
		File findByPathAndTimestamp(String path, Long timestamp);

		List<File> getRecent();
	}
}
