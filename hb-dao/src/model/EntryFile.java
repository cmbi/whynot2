package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class EntryFile extends Entry {
	@NotEmpty
	private String	path;
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date	timestamp;

	protected EntryFile() {}

	public EntryFile(Database db, String pid, String pth, Date time) {
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
