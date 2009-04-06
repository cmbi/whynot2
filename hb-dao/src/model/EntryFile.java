package model;

import java.util.Date;

import javax.persistence.Entity;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class EntryFile extends Entry {
	@NotEmpty
	@Length(max = 200)
	private String	path;
	@NotNull
	private long	lastmodified;

	protected EntryFile() {}

	public EntryFile(Database db, String pid, String pth, long time) {
		super(db, pid);
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
		sb.append(super.toString() + " - ");
		sb.append(path + " - ");
		sb.append(new Date(lastmodified));
		return sb.toString();
	}
}
