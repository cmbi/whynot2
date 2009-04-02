package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Author {
	@Id
	@NotEmpty
	@Length(max = 50)
	private String			name;

	@OneToMany(mappedBy = "author")
	private Set<Comment>	comments	= new HashSet<Comment>();

	protected Author() {}

	public Author(String nm) {
		name = nm;
	}
}
