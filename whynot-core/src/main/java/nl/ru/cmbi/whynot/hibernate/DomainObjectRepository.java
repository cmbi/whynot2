package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import nl.ru.cmbi.whynot.model.*;

/**
 * Data Access Object interfaces. Naming convention: Find - single Get - multiple
 *
 * @param <T>
 *            Type of DomainObject accessible from implementing DAO.
 */
@NoRepositoryBean
public interface DomainObjectRepository<T extends DomainObject> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
	public interface AnnotationRepoCustom {
		long getLastUsed(final Comment comment);

		List<Annotation> getRecent();

		long countWith(final Comment comment);

		List<Entry> getEntriesForComment(final Long l);
	}

	public interface EntryRepoCustom {
		Entry findByDatabankAndPdbid(final Databank databankName, final String pdbid);

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

	public interface FileRepoCustom {
		List<File> getRecent();
	}
}
