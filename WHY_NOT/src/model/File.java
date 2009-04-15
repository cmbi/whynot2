package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@IdClass(EntryPK.class)
public class File {
	@Id
	protected Databank	databank;
	@Id
	@Length(max = 10)
	protected String	pdbid;

	@NotEmpty
	@Length(max = 200)
	protected String	path;
	@NotNull
	private Long		time;

	protected File() {}

	public File(Databank db, String id, String path, Long time) {
		databank = db;
		pdbid = id;
		this.path = path;
		this.time = time;
	}

	public String getPath() {
		return path;
	}

	public Long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return databank + "," + pdbid + "," + path + "," + time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (path == null ? 0 : path.hashCode());
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
		if (path == null) {
			if (other.path != null)
				return false;
		}
		else
			if (!path.equals(other.path))
				return false;
		return true;
	}
}
