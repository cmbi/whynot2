package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Comment {
	@Id
	@NotEmpty
	@Length(max = 200)
	private String		text;

	@ManyToOne
	private Author		author;

	@ManyToMany(mappedBy = "comments")
	private Set<Entry>	entries	= new HashSet<Entry>();

	protected Comment() {}

	public Comment(String comment, Author auth) {
		text = comment;
		author = auth;
	}
}
