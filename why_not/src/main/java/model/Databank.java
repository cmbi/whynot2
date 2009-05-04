package model;

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
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class Databank implements Comparable<Databank> {
	public enum CrawlType {
		FILE, LINE
	}

	@Id
	@GeneratedValue
	public Long				id;

	@NaturalId
	@NotEmpty
	@Length(max = 50)
	public String			name;

	@NotEmpty
	@Length(max = 200)
	public String			reference;
	@NotEmpty
	@Length(max = 200)
	public String			filelink;

	@OneToOne
	@LazyToOne(LazyToOneOption.PROXY)
	public Databank			parent;

	@NotEmpty
	@Length(max = 50)
	public String			regex;

	@NotNull
	@Enumerated(EnumType.STRING)
	public CrawlType		crawltype;

	@OneToMany(mappedBy = "databank", cascade = javax.persistence.CascadeType.ALL)
	@Cascade(value = { CascadeType.DELETE_ORPHAN })
	@Sort(type = SortType.NATURAL)
	public SortedSet<Entry>	entries	= new TreeSet<Entry>();

	public Databank() {
	}

	public Databank(String name, String reference, String filelink, Databank parent, String regex, CrawlType crawltype) {
		this.name = name;
		this.reference = reference;
		this.filelink = filelink;
		this.parent = parent;
		this.regex = regex;
		this.crawltype = crawltype;
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
