package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

@Entity
@IdClass(EntryPK.class)
public class Entry {
	@Id
	private Databank		databank;
	@Id
	private String			pdbid;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Set<Annotation>	annotations	= new HashSet<Annotation>();

	protected Entry() {}

	public Entry(Databank db, String id) {
		databank = db;
		pdbid = id;
	}

	@Override
	public String toString() {
		return databank + "/" + pdbid;
	}

	public Databank getDatabank() {
		return databank;
	}

	public String getPdbid() {
		return pdbid;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}
}
