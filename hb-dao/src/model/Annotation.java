package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;

@Entity
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
