package dao.interfaces;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.NaturalIdentifier;

public interface GenericDAO<T, ID extends Serializable> {

	T findById(ID id, boolean lock);

	T findByNaturalId(NaturalIdentifier id);

	List<T> findAll();

	List<T> findByExample(T exampleInstance, String[] excludeProperty);

	T makePersistent(T entity);

	void makeTransient(T entity);
}
