package dao.interfaces;

import java.util.Set;

import model.Database;
import model.EntryFile;
import dao.hibernate.GenericDAO;

public interface DatabaseDAO extends GenericDAO<Database, String> {
	int getValidCount(Database db);

	Set<EntryFile> getValidEntries(Database db);

	int getMissingCount(Database db);

	Set<EntryFile> getMissingEntries(Database db);

	int getObsoleteCount(Database db);

	Set<EntryFile> getObsoleteEntries(Database db);

}
