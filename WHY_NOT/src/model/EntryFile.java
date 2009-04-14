package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class EntryFile {
	@Id
	protected EntryPK	entry;

	@NotEmpty
	@Length(max = 200)
	private String		path;
	@NotNull
	private long		lastmodified;

	protected EntryFile() {}

	public EntryFile(Database db, String pdbid, String pth, long time) {
		entry = new EntryPK(db, pdbid);
		path = pth;
		lastmodified = time;
	}

	public String getPath() {
		return path;
	}

	public long getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(long time) {
		lastmodified = time;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(entry + " - ");
		sb.append(path + " - ");
		sb.append(new Date(lastmodified));
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (entry == null ? 0 : entry.hashCode());
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
		EntryFile other = (EntryFile) obj;
		if (entry == null) {
			if (other.entry != null)
				return false;
		}
		else
			if (!entry.equals(other.entry))
				return false;
		return true;
	}
}
