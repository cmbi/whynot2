package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
@IdClass(EntryPK.class)
public class Entry {
	@Id
	protected Database	database;
	@Id
	@Length(max=50)
	@NotEmpty
	protected String	pdbid;

	protected Entry() {}

	public Entry(Database database, String pdbid) {
		this.database = database;
		this.pdbid = pdbid;
	}

	@Override
	public String toString() {
		return database + "/" + pdbid;
	}

	public Database getDatabase() {
		return database;
	}

	public String getPdbid() {
		return pdbid;
	}
}
