package nl.ru.cmbi.whynot.mongo;

import java.util.List;
import java.util.SortedSet;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

public interface EntryRepo {

	SortedSet<Entry> findByDatabankName(String databank_name);
	
	Entry findByDatabankAndPdbid(Databank databank, String pdbid);
	boolean contains(String pdbid);
	
	List<Entry> getPresent(final Databank db);
	long countPresent(final Databank db);
	
	List<Entry> getValid(final Databank db);
	long countValid(final Databank db);
	
	List<Entry> getObsolete(final Databank db);
	long countObsolete(final Databank db);
	
	List<Entry> getMissing(final Databank db);
	long countMissing(final Databank db);
	
	List<Entry> getAnnotated(final Databank db);
	long countAnnotated(final Databank db);
	
	List<Entry> getUnannotated(final Databank db);
	long countUnannotated(final Databank db);
	
	List<Entry> findByComment(final Databank db, final String text);
	long countByComment(final Databank db, final String text);
}