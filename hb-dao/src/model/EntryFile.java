package model;

import java.net.URI;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class EntryFile extends Entry {
	private URI		path;
	@Temporal(TemporalType.TIMESTAMP)
	private Date	timestamp;

	protected EntryFile() {}
}
