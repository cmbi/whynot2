package dao.interfaces;

import java.util.List;

import model.Annotation;

public interface AnnotationDAO extends GenericDAO<Annotation, Long> {
	List<Annotation> getRecent();
}
