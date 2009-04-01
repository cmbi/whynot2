package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.NotEmpty;

@Entity
public class Database {
	@Id
	@NotEmpty
	private String		name;

	@NotEmpty
	private String		reference;
	@NotEmpty
	private String		filelink;

	@OneToOne
	private Database	parent;

	@NotEmpty
	private String		pattern;

	@OneToMany(mappedBy = "database", cascade = CascadeType.ALL)
	private Set<Entry>	entries	= new HashSet<Entry>();

	protected Database() {}

	public Database(String nm, String ref, String link, Database par, String pat) {
		name = nm;
		reference = ref;
		filelink = link;
		parent = par;
		pattern = pat;
	}

	@Override
	public String toString() {
		return name;
	}
}
