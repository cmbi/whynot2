package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@IdClass(EntryPK.class)
public class DBFile {
	@Id
	private Databank	databank;
	@Id
	private String		pdbid;

	@OneToOne
	private Entry		entry;

	@NotEmpty
	@Length(max = 200)
	private String		path;
	@NotNull
	private long		lastmodified;

	protected DBFile() {}

	public DBFile(Databank db, String id, String pth, long time) {
		databank = db;
		pdbid = id;
		entry = new Entry(db, id);
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
		sb.append(databank + "/" + pdbid + " - ");
		sb.append(path + " - ");
		sb.append(new Date(lastmodified));
		return sb.toString();
	}
}
