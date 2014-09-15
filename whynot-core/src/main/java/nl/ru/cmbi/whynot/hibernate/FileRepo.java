package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.FileRepoCustom;
import nl.ru.cmbi.whynot.model.File;

public interface FileRepo extends DomainObjectRepository<File>, FileRepoCustom {
	File findByPathAndTimestamp(final String path, final Long timestamp);
}