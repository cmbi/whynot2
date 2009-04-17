package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne
	Databank	databank	= null;
	@Length(max = 10)
	String		pdbid		= null;

	protected EntryPK() {}

	public EntryPK(Databank databank, String pdbid) {
		this.databank = databank;
		this.pdbid = pdbid;
	}

	@Override
	public String toString() {
		return (databank != null ? databank.getName() : null) + "," + pdbid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (databank == null ? 0 : databank.getName().hashCode());
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
		if (databank == null) {
			if (other.databank != null)
				return false;
		}
		else
			if (!databank.getName().equals(other.databank.getName()))
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
