package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class EntryFile {
	@OneToOne
	@PrimaryKeyJoinColumn
	private Entry entry;

	@Id
	@NotEmpty
	@Length(max = 200)
	private String	path;
	@NotNull
	private long	lastmodified;

	protected EntryFile() {}

	public EntryFile(Entry ent, String pth, long time) {
		entry = ent;
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
