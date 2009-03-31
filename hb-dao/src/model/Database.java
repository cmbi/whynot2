package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Database {
	@Id
	private String		name;

	private String		reference;
	private String		filelink;

	@OneToOne
	private Database	parent;

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
}
