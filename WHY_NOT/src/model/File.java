package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@IdClass(EntryPK.class)
public class File implements Comparable<File> {
	@Id
	private Databank	databank;
	@Id
	@Length(max = 10)
	private String		pdbid;

	@NotEmpty
	@Length(max = 200)
	private String		path;
	@NotNull
	private long		timestamp;

	protected File() {}

	public File(Databank db, String id, String path, Long time) {
		databank = db;
		pdbid = id;
		this.path = path;
		timestamp = time;
		databank.getFiles().add(this);
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

	public void setPath(String path) {
		this.path = path;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long time) {
		timestamp = time;
	}

	@Override
	public String toString() {
		return (databank != null ? databank.getName() : null) + "," + pdbid + "," + path + "," + timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (databank == null ? 0 : databank.hashCode());
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
		File other = (File) obj;
		if (databank == null) {
			if (other.databank != null)
				return false;
		}
		else
			if (!databank.equals(other.databank))
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

	public int compareTo(File o) {
		int db = getDatabank().getName().compareTo(o.getDatabank().getName());
		if (db != 0)
			return db;
		return getPdbid().compareTo(o.getPdbid());
	}
}
