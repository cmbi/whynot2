package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.EntryRepoCustom;
import nl.ru.cmbi.whynot.model.Entry;

public interface EntryRepo extends DomainObjectRepository<Entry>, EntryRepoCustom {
}