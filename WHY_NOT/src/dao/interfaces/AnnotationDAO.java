package dao.interfaces;

import java.util.List;

import model.Annotation;
import model.AnnotationPK;

public interface AnnotationDAO extends GenericDAO<Annotation, AnnotationPK> {
	List<Annotation> getRecent();
}
