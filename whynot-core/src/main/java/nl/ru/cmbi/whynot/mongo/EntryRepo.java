package nl.ru.cmbi.whynot.mongo;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Entry.File;

public interface EntryRepo {

	SortedSet<Entry> findByDatabankName(String databank_name);
	
	Entry findByDatabankAndPdbid(Databank databank, String pdbid);
	boolean contains(String pdbid);
	
	List<Entry> getPresent(final Databank db);
	long countPresent(final Databank db);
	long countAllPresent();
	
	List<Entry> getValid(final Databank db);
	long countValid(final Databank db);
	
	List<Entry> getObsolete(final Databank db);
	long countObsolete(final Databank db);
	
	List<Entry> getMissing(final Databank db);
	long countMissing(final Databank db);
	
	List<Entry> getAnnotated(final Databank db);
	long countAnnotated(final Databank db);
	long countAllAnnotated();
	
	List<File> getRecentFiles();
	List<Entry> getRecentlyAnnotated();
	
	List<Entry> getUnannotated(final Databank db);
	long countUnannotated(final Databank db);
	
	List<String> listComments();
	long getLastAnnotation(final String comment);
	
	List<Entry> findWithComment(final String text);
	long countWithComment(final String text);

	long countAll();
}