package model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_text", "database_name", "pdbid" }) })
public class Annotation {
	@Id
	@GeneratedValue
	private int		id;

	@Embedded
	private EntryPK	entry;
	@ManyToOne
	private Comment	comment;
	@ManyToOne
	private Author	author;

	private long	timestamp	= System.currentTimeMillis();

	protected Annotation() {}

	public Annotation(Database db, String pdbid, Comment com, Author aut) {
		entry = new EntryPK(db, pdbid);
		comment = com;
		author = aut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (comment == null ? 0 : comment.hashCode());
		result = prime * result + (entry == null ? 0 : entry.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		}
		else
			if (!comment.equals(other.comment))
				return false;
		if (entry == null) {
			if (other.entry != null)
				return false;
		}
		else
			if (!entry.equals(other.entry))
				return false;
		return true;
	}
}
