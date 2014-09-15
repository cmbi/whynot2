package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

public interface EntryRepo extends DomainObjectRepository<Entry>, DomainObjectRepository.EntryRepoCustom {
	Entry findByDatabankAndPdbid(final Databank databank, final String pdbid);
}