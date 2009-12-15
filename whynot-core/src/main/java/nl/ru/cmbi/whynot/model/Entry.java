package nl.ru.cmbi.whynot.model;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Entry implements Comparable<Entry>, Serializable {
	@Id
	@GeneratedValue(generator = "hibseq")
	@GenericGenerator(name = "hibseq", strategy = "seqhilo", parameters = { @Parameter(name = "max_lo", value = "50"), })
	Long							id;

	@NaturalId
	@ManyToOne
	@NotNull
	@LazyToOne(LazyToOneOption.PROXY)
	@Index(name = "entry_databank_index")
	private Databank				databank;
	@NaturalId
	@Length(max = 10)
	@NotNull
	@Index(name = "entry_pdbid_index")
	private String					pdbid;

	@ManyToOne(cascade = CascadeType.ALL)
	@LazyToOne(LazyToOneOption.PROXY)
	private File					file;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@Sort(type = SortType.NATURAL)
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Entry() {}

	public Entry(Databank db, String id) {
		databank = db;
		pdbid = id.toLowerCase();
	}

	public Databank getDatabank() {
		return databank;
	}

	public String getPdbid() {
		return pdbid;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public SortedSet<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public String toString() {
		return databank.getName() + "," + pdbid;
	}

	@Override
	public int compareTo(Entry o) {
		int value = databank.compareTo(o.databank);
		if (value != 0)
			return value;
		return pdbid.compareTo(o.pdbid);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Entry other = (Entry) obj;
		if (databank == null) {
			if (other.databank != null)
				return false;
		}
		else
			if (!databank.getName().equals(other.databank.getName()))
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
