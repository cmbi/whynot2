package dao.interfaces;

import java.util.Set;

import model.Database;
import model.EntryFile;
import dao.hibernate.GenericDAO;

public interface DatabaseDAO extends GenericDAO<Database, String> {
	Set<EntryFile> getValidEntries(Database db);

	Set<EntryFile> getMissingEntries(Database db);

	Set<EntryFile> getObsoleteEntries(Database db);

}
