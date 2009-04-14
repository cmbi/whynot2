package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne
	private Database	database;
	private String		pdbid;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (database == null ? 0 : database.getName().hashCode());
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
