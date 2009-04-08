package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne
	protected Database	database;

	@Length(max = 50)
	@NotEmpty
	protected String	pdbid;

	protected EntryPK() {}

	public EntryPK(Database db, String pid) {
		database = db;
		pdbid = pid;
	}

	@Override
	public String toString() {
		return database + "/" + pdbid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (database == null ? 0 : database.hashCode());
		result = prime * result + (pdbid == null ? 0 : pdbid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntryPK other = (EntryPK) obj;
		if (database == null) {
			if (other.database != null)
				return false;
		}
		else
			if (!database.equals(other.database))
				return false;
		if (pdbid == null) {
			if (other.pdbid != null)
				return false;
		}
		else
			if (!pdbid.equals(other.pdbid))
				return false;
		return true;
	}
}
