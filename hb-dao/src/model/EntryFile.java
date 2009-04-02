package model;

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
	private long	timestamp;

	protected EntryFile() {}

	public EntryFile(Database db, String pid, String pth, long time) {
		super(db, pid);
		path = pth;
		timestamp = time;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pdbid + " - ");
		sb.append(database + " - ");
		sb.append(path + " - ");
		sb.append(timestamp);
		return sb.toString();
	}
}
