package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.NotNull;

@Entity
public class Annotation implements Comparable<Annotation> {
	@Id
	@GeneratedValue
	Long			id;

	@NaturalId
	@ManyToOne
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@NotNull
	private Comment	comment;
	@NaturalId
	@ManyToOne
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@NotNull
	private Entry	entry;

	private long	timestamp;

	protected Annotation() {
	}

	public Annotation(Comment comment, Entry entry, long timestamp) {
		this.comment = comment;
		this.entry = entry;
		this.timestamp = timestamp;
	}

	public Comment getComment() {
		return comment;
	}

	public Entry getEntry() {
		return entry;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return comment + "," + entry + "," + timestamp;
	}

	public int compareTo(Annotation o) {
		int value = getComment().compareTo(o.getComment());
		if (value != 0)
			return value;
		return getEntry().compareTo(o.getEntry());
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
