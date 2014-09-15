package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = "name")
@SuppressWarnings("unused")
@ToString(exclude = { "parent", "entries" })
public class Databank extends DomainObject implements Comparable<Databank> {
	public enum CollectionType {
		PRESENT, VALID, OBSOLETE, MISSING, ANNOTATED, UNANNOTATED
	}

	public enum CrawlType {
		FILE, LINE
	}

	@NaturalId
	@NotEmpty
	@Length(max = 50)
	@Setter(AccessLevel.NONE)
	private String					name;

	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String					reference;

	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String					filelink;

	@OneToOne
	@LazyToOne(LazyToOneOption.PROXY)
	@Setter(AccessLevel.NONE)
	private Databank				parent;

	@NotEmpty
	@Length(max = 50)
	@Setter(AccessLevel.NONE)
	private String					regex;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Setter(AccessLevel.NONE)
	private CrawlType				crawltype;

	@OneToMany(mappedBy = "databank", cascade = CascadeType.ALL, orphanRemoval = true)
	@SortNatural
	@Setter(AccessLevel.NONE)
	private final SortedSet<Entry>	entries	= new TreeSet<Entry>();

	protected Databank() {/* Hibernate requirement */
	}

	@Deprecated
	public Databank(final String name) {
		this.name = name;
	}

	public Databank(final String name, final CrawlType crawltype, final String regex, final String reference, final String filelink) {
		this(name, null, crawltype, regex, reference, filelink);
		parent = this;
	}

	public Databank(final String name, final Databank parent, final CrawlType crawltype, final String regex, final String reference, final String filelink) {
		this.name = name;
		this.reference = reference;
		this.filelink = filelink;
		this.parent = parent;
		this.regex = regex;
		this.crawltype = crawltype;
	}

	@Override
	public int compareTo(final Databank o) {
		return name.compareTo(o.name);
	}
}
