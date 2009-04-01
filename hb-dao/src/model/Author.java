package model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.NotEmpty;

@Entity
public class Author {
	@Id
	@NotEmpty
	private String			name;

	@OneToMany(mappedBy = "author")
	private Set<Comment>	comments;

	protected Author() {}

	public Author(String nm) {
		name = nm;
	}
}
