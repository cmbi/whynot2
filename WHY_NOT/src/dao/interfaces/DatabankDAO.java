package dao.interfaces;

import java.util.Set;
import java.util.SortedSet;

import model.Databank;
import model.File;

public interface DatabankDAO extends GenericDAO<Databank, String> {
	long getValidCount(Databank db);

	Set<File> getValidEntries(Databank db);

	Set<File> getValidEntriesWith(Databank db);

	Set<File> getValidEntriesWithout(Databank db);

	long getMissingCount(Databank db);

	SortedSet<File> getMissingEntries(Databank db);

	long getObsoleteCount(Databank db);

	Set<File> getObsoleteEntries(Databank db);
}
