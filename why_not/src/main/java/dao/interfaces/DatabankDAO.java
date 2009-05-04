package dao.interfaces;

import java.util.SortedSet;

import model.Databank;
import model.File;

public interface DatabankDAO extends GenericDAO<Databank, Long> {
	public enum CollectionType {
		ALL, VALID, MISSING, OBSOLETE
	};

	public enum AnnotationType {
		ALL, WITH, WITHOUT
	};

	long getCount(Databank db, AnnotationType at);

	long getValidCount(Databank db, AnnotationType at);

	long getMissingCount(Databank db, AnnotationType at);

	long getObsoleteCount(Databank db, AnnotationType at);

	SortedSet<File> getEntries(Databank db, AnnotationType all);

	SortedSet<File> getValidEntries(Databank db, AnnotationType at);

	SortedSet<File> getMissingEntries(Databank db, AnnotationType at);

	SortedSet<File> getObsoleteEntries(Databank db, AnnotationType at);
}
