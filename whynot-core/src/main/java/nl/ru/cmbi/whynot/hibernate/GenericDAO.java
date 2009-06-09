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

		long getLatest(Comment comment);

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

		@Transactional
		int removeEntriesWithoutBothFileAndParentFile();

		Entry getParent(Entry entry);

		List<Entry> getChildren(Entry entry);

		List<Entry> getValid(Databank child);

		List<Entry> getObsolete(Databank child);

		List<Entry> getMissing(Databank child);

		List<Entry> getAnnotated(Databank child);

		List<Entry> getUnannotated(Databank child);

		long getValidCount(Databank child);

		long getObsoleteCount(Databank child);

		long getMissingCount(Databank child);

		long getUnannotatedCount(Databank child);

		long getAnnotatedCount(Databank child);
	}

	public interface FileDAO extends GenericDAO<File, Long> {
		File findByPathAndTimestamp(String path, Long timestamp);

		List<File> getRecent();
	}
}
