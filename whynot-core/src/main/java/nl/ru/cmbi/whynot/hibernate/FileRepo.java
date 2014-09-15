package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.FileRepoCustom;
import nl.ru.cmbi.whynot.model.File;

public interface FileRepo extends DomainObjectRepository<File>, DomainObjectRepository.FileRepoCustom {}