package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.*;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = "text")
@ToString(of="text")
public class Comment extends DomainObject implements Comparable<Comment> {
	@NaturalId
	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String					text;

	@OneToMany(mappedBy = "comment")
	@Sort(type = SortType.NATURAL)
	@Setter(AccessLevel.NONE)
	@SuppressWarnings("unused")
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Comment() {/*Hibernate requirement*/}

	public Comment(final String text) {
		this.text = text;
	}

	@Override
	public int compareTo(final Comment o) {
		return text.compareTo(o.text);
	}
}
