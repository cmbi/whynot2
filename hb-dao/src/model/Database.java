package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Database {
	@Id
	@NotEmpty
	@Length(max = 50)
	private String		name;

	@NotEmpty
	@Length(max = 200)
	private String		reference;
	@NotEmpty
	@Length(max = 200)
	private String		filelink;

	@OneToOne
	private Database	parent;

	@NotEmpty
	@Length(max = 50)
	private String		regex;

	@OneToMany(mappedBy = "database", cascade = CascadeType.ALL)
	private Set<Entry>	entries	= new HashSet<Entry>();

	protected Database() {}

	public Database(String nm, String ref, String link, Database par, String pat) {
		name = nm;
		reference = ref;
		filelink = link;
		parent = par;
		regex = pat;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getRegex() {
		return regex;
	}
}
