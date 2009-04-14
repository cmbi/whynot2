package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Author {
	@Id
	@NotEmpty
	@Length(max = 50)
	private String			name;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Set<Annotation>	annotations	= new HashSet<Annotation>();

	protected Author() {}

	public Author(String nm) {
		name = nm;
	}

	public String getName() {
		return name;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}
}
