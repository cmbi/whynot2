package old_model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@IdClass(EntryPK.class)
public class Entry {
	@Id
	private Databank	databank;
	@Id
	private String		pdbid;

	@NotEmpty
	@Length(max = 200)
	private String		path			= null;
	@NotNull
	private Long		lastmodified	= null;

	protected Entry() {}

	public Entry(Databank db, String id) {
		databank = db;
		pdbid = id;
	}

	public Entry(Databank db, String id, String pth, long lm) {
		this(db, id);
		path = pth;
		lastmodified = lm;
	}

	public Databank getDatabank() {
		return databank;
	}

	public String getPdbid() {
		return pdbid;
	}

	public String getPath() {
		return path;
	}

	public Long getLastmodified() {
		return lastmodified;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(databank + "/" + pdbid);
		sb.append(" - " + path);
		sb.append(" - " + lastmodified);
		return sb.toString();
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
		Entry other = (Entry) obj;
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
