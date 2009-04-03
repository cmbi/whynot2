package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Entry {
	@EmbeddedId
	protected EntryPK		entryPK;

	@ManyToMany
	private Set<Comment>	comments	= new HashSet<Comment>();

	protected Entry() {}

	public Entry(Database db, String pid) {
		entryPK = new EntryPK(db, pid);

	}

	public Set<Comment> getComments() {
		return comments;
	}
}
