package model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne(cascade = CascadeType.ALL)
	private Database	database;

	@Length(max = 50)
	@NotEmpty
	private String		pdbid;

	protected EntryPK() {}

	public EntryPK(Database db, String pid) {
		database = db;
		pdbid = pid;
	}

	@Override
	public String toString() {
		return database + "/" + pdbid;
	}
}
