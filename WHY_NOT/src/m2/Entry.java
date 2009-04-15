package m2;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
@IdClass(EntryPK.class)
public class Entry {
	@Id
	protected Databank		databank;
	@Id
	private String			pdbid;

	@OneToOne(mappedBy = "entry")
	File					file;

	@ManyToMany(mappedBy = "entries")
	private Set<Annotation>	annotations	= new HashSet<Annotation>();
}
