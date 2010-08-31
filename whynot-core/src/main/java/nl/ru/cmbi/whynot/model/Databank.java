package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = "name")
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
	private String				name;

	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String				reference;

	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String				filelink;

	@OneToOne
	@LazyToOne(LazyToOneOption.PROXY)
	@Setter(AccessLevel.NONE)
	private Databank			parent;

	@NotEmpty
	@Length(max = 50)
	@Setter(AccessLevel.NONE)
	private String				regex;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Setter(AccessLevel.NONE)
	private CrawlType			crawltype;

	@OneToMany(mappedBy = "databank", cascade = javax.persistence.CascadeType.ALL)
	@Cascade(value = { CascadeType.DELETE_ORPHAN })
	@Sort(type = SortType.NATURAL)
	@Setter(AccessLevel.NONE)
	private SortedSet<Entry>	entries	= new TreeSet<Entry>();

	protected Databank() {/*Hibernate requirement*/}

	public Databank(String name) {
		this.name = name;
	}

	public Databank(String name, CrawlType crawltype, String regex, String reference, String filelink) {
		this(name, null, crawltype, regex, reference, filelink);
		parent = this;
	}

	public Databank(String name, Databank parent, CrawlType crawltype, String regex, String reference, String filelink) {
		this.name = name;
		this.reference = reference;
		this.filelink = filelink;
		this.parent = parent;
		this.regex = regex;
		this.crawltype = crawltype;
	}

	@Override
	public int compareTo(Databank o) {
		return name.compareTo(o.name);
	}
}
