package dao.interfaces;

import java.util.Set;

import model.Databank;
import model.File;

public interface DatabankDAO extends GenericDAO<Databank, String> {
	public enum AnnotationType {
		ALL, WITH, WITHOUT
	};

	long getValidCount(AnnotationType at, Databank db);

	long getMissingCount(AnnotationType at, Databank db);

	long getObsoleteCount(AnnotationType at, Databank db);

	Set<File> getValidEntries(AnnotationType at, Databank db);

	Set<File> getMissingEntries(AnnotationType at, Databank db);

	Set<File> getObsoleteEntries(AnnotationType at, Databank db);
}
