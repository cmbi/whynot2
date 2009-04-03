package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "entry_database_name", "comment_text", "entry_pdbid" }) })
public class Annotation {
	@Id
	@GeneratedValue
	private int		id;

	@NotNull
	@ManyToOne
	private Entry	entry;
	@NotNull
	@ManyToOne
	private Comment	comment;
	@NotNull
	@ManyToOne
	private Author	author;

	private long	timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Entry ent, Comment com, Author aut) {
		comment = com;
		author = aut;
		entry = ent;
	}
}
