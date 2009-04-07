package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Comment {
	@Id
	@NotEmpty
	@Length(max = 200)
	private String			text;

	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
	private Set<Annotation>	annotations	= new HashSet<Annotation>();

	protected Comment() {}

	public Comment(String content) {
		text = content;
	}
}
