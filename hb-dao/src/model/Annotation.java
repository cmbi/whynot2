package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_text", "database_name", "pdbid" }) })
public class Annotation {
	@EmbeddedId
	protected EntryPK	entry;

	@ManyToOne
	private Comment		comment;
	@ManyToOne
	private Author		author;

	private long		timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Database db, String pdbid, Comment com, Author aut) {
		entry = new EntryPK(db, pdbid);
		comment = com;
		author = aut;
	}
}
