package model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Database {
	@Id
	private String	name;

	//	private URL				reference;
	//	private URL				filelink;
	//	private Database		parent;
	//
	//	private Pattern			filepattern;
	//	private Set<EntryFile>	entries;

	protected Database() {}
}
