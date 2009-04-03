package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Entry {
	@EmbeddedId
	protected EntryPK		entryPK;

	@OneToMany(mappedBy = "entry")
	private Set<Annotation>	annotations	= new HashSet<Annotation>();

	protected Entry() {}

	public Entry(Database db, String pbdid) {
		entryPK = new EntryPK(db, pbdid);
	}
}
