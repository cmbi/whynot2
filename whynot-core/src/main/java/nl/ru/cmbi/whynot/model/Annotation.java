package nl.ru.cmbi.whynot.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.NotNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = { "comment", "entry" })
public class Annotation extends DomainObject implements Comparable<Annotation> {
	@NaturalId
	@ManyToOne
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@NotNull
	@Index(name = "annotation_comment_index")
	@Setter(AccessLevel.NONE)
	private Comment	comment;

	@NaturalId
	@ManyToOne
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@NotNull
	@Index(name = "annotation_entry_index")
	@Setter(AccessLevel.NONE)
	private Entry	entry;

	@Setter(AccessLevel.NONE)
	private Long	timestamp;

	protected Annotation() {/*Hibernate requirement*/}

	public Annotation(Comment comment, Entry entry, Long timestamp) {
		this.comment = comment;
		this.entry = entry;
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(Annotation o) {
		int value = comment.compareTo(o.comment);
		if (value != 0)
			return value;
		return entry.compareTo(o.entry);
	}
}
