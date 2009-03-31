package model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Author {
	@Id
	private String			name;

	@OneToMany(mappedBy = "author")
	private Set<Comment>	comments;

	protected Author() {}

	public Author(String nm) {
		name = nm;
	}
}
