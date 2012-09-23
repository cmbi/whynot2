package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = { "databank", "pdbid" })
public class Entry extends DomainObject implements Comparable<Entry> {
	@NaturalId
	@ManyToOne
	@NotNull
	@LazyToOne(LazyToOneOption.PROXY)
	@Index(name = "entry_databank_index")
	@Setter(AccessLevel.NONE)
	private Databank				databank;

	@NaturalId
	@Length(max = 10)
	@NotNull
	@Index(name = "entry_pdbid_index")
	@Setter(AccessLevel.NONE)
	private String					pdbid;

	@ManyToOne(cascade = CascadeType.ALL)
	@LazyToOne(LazyToOneOption.PROXY)
	@SuppressWarnings("unused")
	private File					file;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
	@Sort(type = SortType.NATURAL)
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
