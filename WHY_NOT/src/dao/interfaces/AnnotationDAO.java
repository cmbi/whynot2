package dao.interfaces;

import java.util.List;

import model.Annotation;
import dao.hibernate.GenericDAO;

public interface AnnotationDAO extends GenericDAO<Annotation, Integer> {
	List<Annotation> getRecent();
}
