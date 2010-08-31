package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = "text")
@ToString(exclude="annotations")
public class Comment extends DomainObject implements Comparable<Comment> {
	@NaturalId
	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String					text;

	@OneToMany(mappedBy = "comment")
	@Sort(type = SortType.NATURAL)
	@Setter(AccessLevel.NONE)
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Comment() {/*Hibernate requirement*/}

	public Comment(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(Comment o) {
		return text.compareTo(o.text);
	}
}
