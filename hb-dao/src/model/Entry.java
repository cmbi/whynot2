package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

@Entity
@IdClass(EntryPK.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class Entry {
	@Id
	private Database		database;
	@Id
	private String			pdbid;

	@ManyToMany
	private Set<Comment>	comments	= new HashSet<Comment>();

	protected Entry() {}

	public Entry(Database db, String pid) {
		database = db;
		pdbid = pid;
	}

	public Set<Comment> getComments() {
		return comments;
	}
}
