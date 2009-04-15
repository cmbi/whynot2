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
	private Databank	databank;
	@Id
	private String		pdbid;
	@Id
	private Comment		comment;

	@ManyToOne
	private Author		author;

	private long		timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Databank db, String id, Comment com, Author aut) {
		databank = db;
		pdbid = id;
		comment = com;
		author = aut;
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
		sb.append(databank + "/" + pdbid);
		sb.append(" - " + comment);
		sb.append("\n\t" + author);
		sb.append(", " + new Date(timestamp));
		return sb.toString();
	}
}
