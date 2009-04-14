package dao.interfaces;

import java.util.Set;

import model.Database;
import model.EntryFile;
import dao.hibernate.GenericDAO;

public interface DatabaseDAO extends GenericDAO<Database, String> {
	long getValidCount(Database db);

	Set<EntryFile> getValidEntries(Database db);

	long getMissingCount(Database db);

	Set<EntryFile> getMissingEntries(Database db);

	long getObsoleteCount(Database db);

	Set<EntryFile> getObsoleteEntries(Database db);

}
