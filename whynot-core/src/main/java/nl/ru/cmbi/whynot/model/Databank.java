package nl.ru.cmbi.whynot.model;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class Databank implements Comparable<Databank>, Serializable {
	public enum CollectionType {
		PRESENT, VALID, OBSOLETE, MISSING, ANNOTATED, UNANNOTATED
	}

	public enum CrawlType {
		FILE, LINE
	}

	@Id
	@GeneratedValue(generator = "hibseq")
	@GenericGenerator(name = "hibseq", strategy = "seqhilo", parameters = { @Parameter(name = "max_lo", value = "50"), })
	Long						id;

	@NaturalId
	@NotEmpty
	@Length(max = 50)
	private String				name;

	@NotEmpty
	@Length(max = 200)
	private String				reference;
	@NotEmpty
	@Length(max = 200)
	private String				filelink;

	@OneToOne
	@LazyToOne(LazyToOneOption.PROXY)
	private Databank			parent;

	@NotEmpty
	@Length(max = 50)
	private String				regex;

	@NotNull
	@Enumerated(EnumType.STRING)
	private CrawlType			crawltype;

	@OneToMany(mappedBy = "databank", cascade = javax.persistence.CascadeType.ALL)
	@Cascade(value = { CascadeType.DELETE_ORPHAN })
	@Sort(type = SortType.NATURAL)
	private SortedSet<Entry>	entries	= new TreeSet<Entry>();

	protected Databank() {
	}

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

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getReference() {
		return reference;
	}

	public String getFilelink() {
		return filelink;
	}

	public Databank getParent() {
		return parent;
	}

	public String getRegex() {
		return regex;
	}

	public CrawlType getCrawltype() {
		return crawltype;
	}

	public SortedSet<Entry> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return name + "," + reference + "," + filelink + "," + (parent != null ? parent.getName() : null) + "," + regex + "," + crawltype;
	}

	public int compareTo(Databank o) {
		return name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		Databank other = (Databank) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else
			if (!name.equals(other.name))
				return false;
		return true;
	}
}
