package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = { "databank", "pdbid" })
@Table(indexes = {
		@Index(name = "entry_databank_index", columnList = "databank_id"),
		@Index(name = "entry_pdbid_index", columnList = "pdbid")
})
public class Entry extends DomainObject implements Comparable<Entry> {
	@NaturalId
	@ManyToOne
	@NotNull
	@Setter(AccessLevel.NONE)
	private Databank				databank;

	@NaturalId
	@Length(max = 10)
	@NotNull
	@Setter(AccessLevel.NONE)
	private String					pdbid;

	@ManyToOne(cascade = CascadeType.ALL)
	@SuppressWarnings("unused")
	private File					file;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
	@SortNatural
	@Setter(AccessLevel.NONE)
	@SuppressWarnings("unused")
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Entry() {/* Hibernate requirement */
	}

	public Entry(final Databank db, final String id) {
		databank = db;
		pdbid = id.toLowerCase();
	}

	@Override
	public int compareTo(final Entry o) {
		int value = databank.compareTo(o.databank);
		if (value != 0)
			return value;
		return pdbid.compareTo(o.pdbid);
	}

	@Override
	public String toString() {
		return databank.getName() + "," + pdbid;
	}
}
