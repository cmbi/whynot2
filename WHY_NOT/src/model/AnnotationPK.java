package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Embeddable
public class AnnotationPK implements Serializable {
	@ManyToOne
	@Cascade(value = { CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	Comment	comment;

	@ManyToOne
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	Entry	entry;

	protected AnnotationPK() {}

	public AnnotationPK(Comment comment, Entry entry) {
		this.comment = comment;
		this.entry = entry;
	}

	@Override
	public String toString() {
		return comment + "," + entry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (comment == null ? 0 : comment.getText().hashCode());
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
		AnnotationPK other = (AnnotationPK) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		}
		else
			if (!comment.getText().equals(other.comment.getText()))
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
