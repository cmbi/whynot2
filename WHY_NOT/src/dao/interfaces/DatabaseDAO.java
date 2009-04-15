package dao.interfaces;

import java.util.Set;

import model.Databank;
import model.DBFile;
import dao.hibernate.GenericDAO;

public interface DatabaseDAO extends GenericDAO<Databank, String> {
	long getValidCount(Databank db);

	Set<DBFile> getValidEntries(Databank db);

	long getMissingCount(Databank db);

	Set<DBFile> getMissingEntries(Databank db);

	long getObsoleteCount(Databank db);

	Set<DBFile> getObsoleteEntries(Databank db);

}
