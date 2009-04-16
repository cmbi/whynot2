package dao.interfaces;

import java.util.List;

import model.Annotation;
import model.AnnotationPK;
import dao.hibernate.GenericDAO;

public interface AnnotationDAO extends GenericDAO<Annotation, AnnotationPK> {
	List<Annotation> getRecent();
}
