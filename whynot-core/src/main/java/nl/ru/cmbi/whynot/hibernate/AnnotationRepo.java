package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository.AnnotationRepoCustom;
import nl.ru.cmbi.whynot.model.Annotation;

// Interfaces
public interface AnnotationRepo extends DomainObjectRepository<Annotation>, DomainObjectRepository.AnnotationRepoCustom {}