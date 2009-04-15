package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Entity
@IdClass(AnnotationPK.class)
public class Annotation {
	@Id
	private Entry	entry;
	@Id
	private Comment	comment;

	@ManyToOne
	private Author	author;

	private long	timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Entry ent, Comment com, Author aut) {
		entry = ent;
		comment = com;
		author = aut;
	}

	public Entry getEntry() {
		return entry;
	}

	public Comment getComment() {
		return comment;
	}

	public Author getAuthor() {
		return author;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(entry + " - ");
		sb.append(comment + "\n\t");
		sb.append(author + ", " + new Date(timestamp));
		return sb.toString();
	}
}
