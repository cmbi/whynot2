package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class EntryFile extends Entry {
	private String	path;
	@Temporal(TemporalType.TIMESTAMP)
	private Date	timestamp;

	protected EntryFile() {}

	public EntryFile(Database db, String pid, String pth, Date time) {
		super(db, pid);
		path = pth;
		timestamp = time;
	}
}
