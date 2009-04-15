package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@IdClass(AnnotationPK.class)
public class Annotation {
	@Id
	protected Author	author;
	@Id
	protected Comment	comment;

	private long		timestamp	= System.currentTimeMillis();

	@ManyToMany
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	private Set<Entry>	entries		= new HashSet<Entry>();

	protected Annotation() {}

	public Annotation(Author author, Comment comment) {
		this.author = author;
		this.comment = comment;
	}

	public Set<Entry> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return author + "," + comment + "," + timestamp;
	}
}
