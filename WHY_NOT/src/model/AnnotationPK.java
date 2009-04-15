package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

@Embeddable
public class AnnotationPK implements Serializable {
	@ManyToOne
	protected Databank	databank;
	@Length(min = 4, max = 50)
	protected String	pdbid;
	@ManyToOne
	protected Comment	comment;

	protected AnnotationPK() {}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (comment == null ? 0 : comment.hashCode());
		result = prime * result + (databank == null ? 0 : databank.hashCode());
		result = prime * result + (pdbid == null ? 0 : pdbid.hashCode());
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
			if (!comment.equals(other.comment))
				return false;
		if (databank == null) {
			if (other.databank != null)
				return false;
		}
		else
			if (!databank.equals(other.databank))
				return false;
		if (pdbid == null) {
			if (other.pdbid != null)
				return false;
		}
		else
			if (!pdbid.equals(other.pdbid))
				return false;
		return true;
	}
}
