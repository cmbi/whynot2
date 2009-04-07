package model;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class EntryFile {
	@EmbeddedId
	protected EntryPK	entry;

	@NotEmpty
	@Length(max = 200)
	private String		path;
	@NotNull
	private long		lastmodified;

	protected EntryFile() {}

	public EntryFile(EntryPK entpk, String pth, long time) {
		entry = entpk;
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
}
