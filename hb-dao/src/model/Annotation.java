package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_text", "database_name", "pdbid" }) })
public class Annotation {
	@EmbeddedId
	protected EntryPK	entry;

	@NotNull
	@ManyToOne
	private Comment		comment;
	@NotNull
	@ManyToOne
	private Author		author;

	private long		timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(EntryPK entpk, Comment com, Author aut) {
		entry = entpk;
		comment = com;
		author = aut;
	}
}
