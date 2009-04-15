package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;

@Entity
@IdClass(AnnotationPK.class)
public class Annotation {
	@Id
	protected Author	author;
	@Id
	protected Comment	comment;

	private long		timestamp	= System.currentTimeMillis();

	@ManyToMany
	private Set<Entry>	entries		= new HashSet<Entry>();

	@Override
	public String toString() {
		return author + "," + comment + "," + timestamp;
	}
}
