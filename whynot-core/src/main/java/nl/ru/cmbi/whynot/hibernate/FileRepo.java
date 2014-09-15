package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.model.File;

public interface FileRepo extends DomainObjectRepository<File>, DomainObjectRepository.FileRepoCustom {
	File findByPathAndTimestamp(final String path, final Long timestamp);
}