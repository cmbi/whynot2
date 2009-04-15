package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class AnnotationPK implements Serializable {
	@ManyToOne
	Author	author;
	@ManyToOne
	Comment	comment;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (author == null ? 0 : author.name.hashCode());
		result = prime * result + (comment == null ? 0 : comment.text.hashCode());
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
		if (author == null) {
			if (other.author != null)
				return false;
		}
		else
			if (!author.name.equals(other.author.name))
				return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		}
		else
			if (!comment.text.equals(other.comment.text))
				return false;
		return true;
	}
}
