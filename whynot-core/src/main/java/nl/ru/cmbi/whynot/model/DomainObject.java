package nl.ru.cmbi.whynot.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DomainObject implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long	id;

	public Long getId() {
		return id;
	}

	@Override
	public abstract boolean equals(final Object obj);

	@Override
	public abstract int hashCode();

	@Override
	public abstract String toString();
}
