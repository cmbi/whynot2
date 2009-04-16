package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@IdClass(AnnotationPK.class)
public class Annotation {
	@ManyToOne
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	private Author	author;

	@Id
	private Comment	comment;
	@Id
	private Entry	entry;

	private long	timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Author author, Comment comment, Entry entry) {
		this.author = author;
		this.comment = comment;
		this.entry = entry;
		entry.getAnnotations().add(this);
	}

	public Annotation(Author author, Comment comment, Entry entry, long timestamp) {
		this(author, comment, entry);
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return author + "," + comment + "," + entry + "," + timestamp;
	}
}
