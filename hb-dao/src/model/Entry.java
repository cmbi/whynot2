package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Entry {
	@EmbeddedId
	protected EntryPK		entryPK;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Set<Annotation>	annotations	= new HashSet<Annotation>();

	protected Entry() {}

	public Entry(Database db, String pbdid) {
		entryPK = new EntryPK(db, pbdid);
	}

	@Override
	public String toString() {
		return entryPK.database + "/" + entryPK.pdbid;
	}
}
