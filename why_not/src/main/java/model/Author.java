package model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Author implements Comparable<Author> {
	@Id
	@GeneratedValue
	Long							id;

	@NaturalId
	@NotEmpty
	@Length(max = 50)
	private String					name;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@Sort(type = SortType.NATURAL)
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Author() {
	}

	public Author(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public SortedSet<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public String toString() {
		return name;
	}

	public int compareTo(Author o) {
		return name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else
			if (!name.equals(other.name))
				return false;
		return true;
	}
}
