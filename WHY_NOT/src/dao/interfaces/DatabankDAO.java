package dao.interfaces;

import java.util.Set;

import old_model.Databank;
import old_model.Entry;

import dao.hibernate.GenericDAO;

public interface DatabankDAO extends GenericDAO<Databank, String> {
	long getValidCount(Databank db);

	Set<Entry> getValidEntries(Databank db);

	long getMissingCount(Databank db);

	Set<Entry> getMissingEntries(Databank db);

	long getObsoleteCount(Databank db);

	Set<Entry> getObsoleteEntries(Databank db);
}
