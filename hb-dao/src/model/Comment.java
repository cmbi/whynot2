package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
	@Id
	private String	text;

	@ManyToOne
	private Author	author;

	//@ManyToMany
	//private Set<Entry>	entries;

	protected Comment() {}
}
