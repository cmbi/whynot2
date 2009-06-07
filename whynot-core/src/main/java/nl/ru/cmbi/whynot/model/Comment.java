package nl.ru.cmbi.whynot.model;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Comment implements Comparable<Comment>, Serializable {
	@Id
	@GeneratedValue(generator = "hibseq")
	@GenericGenerator(name = "hibseq", strategy = "seqhilo", parameters = { @Parameter(name = "max_lo", value = "50"), })
	Long							id;

	@NaturalId
	@NotEmpty
	@Length(max = 200)
	private String					text;

	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
	//FIXME cascade in comment
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@Sort(type = SortType.NATURAL)
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Comment() {
	}

	public Long getId() {
		return id;
	}

	public Comment(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public SortedSet<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public String toString() {
		return text;
	}

	public int compareTo(Comment o) {
		return text.compareTo(o.text);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (text == null ? 0 : text.hashCode());
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
		Comment other = (Comment) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		}
		else
			if (!text.equals(other.text))
				return false;
		return true;
	}
}
