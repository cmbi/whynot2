package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(Entry.class)
public class EntryComments {
	@Id
	private String		pdbid;

	@Id
	private Database	database;

	//@ManyToMany
	//private Set<Comment>	comments;

	protected EntryComments() {}
}
