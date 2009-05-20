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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@FilterDefs( {//
@FilterDef(name = "inDatabank", defaultCondition = "databank_id = (select db.id from Databank db where db.name like :name)", parameters = @ParamDef(name = "name", type = "string")), //

@FilterDef(name = "withFile", defaultCondition = "file_id is not null"), //
@FilterDef(name = "withoutFile", defaultCondition = "file_id is null"), //

@FilterDef(name = "withParentFile", defaultCondition = "(select par.file_id from Entry par where par.pdbid = pdbid and par.databank_id = (select db.parent_id from Databank db where db.id = databank_id)) is not null"), //
@FilterDef(name = "withoutParentFile", defaultCondition = "(select par.file_id from Entry par where par.pdbid = pdbid and par.databank_id = (select db.parent_id from Databank db where db.id = databank_id)) is null"), //

@FilterDef(name = "withComment", defaultCondition = "id in (select distinct ann.entry_id from Annotation ann)"), //
@FilterDef(name = "withoutComment", defaultCondition = "id not in (select distinct ann.entry_id from Annotation ann)"), //

@FilterDef(name = "withThisComment", defaultCondition = "id in (select distinct ann.entry_id from Annotation ann where ann.comment_id in (select com.id from Comment com where com.text like :comment))", parameters = @ParamDef(name = "comment", type = "string")), //
@FilterDef(name = "withoutThisComment", defaultCondition = "id not in (select distinct ann.entry_id from Annotation ann where ann.comment_id in (select com.id from Comment com where com.text like :comment))", parameters = @ParamDef(name = "comment", type = "string")) })
@Filters( { //
@Filter(name = "inDatabank"),//
@Filter(name = "withFile"), @Filter(name = "withoutFile"),//
@Filter(name = "withParentFile"), @Filter(name = "withoutParentFile"),//	
@Filter(name = "withComment"), @Filter(name = "withoutComment"),//	
@Filter(name = "withThisComment"), @Filter(name = "withoutThisComment") //	
})
public class Entry implements Comparable<Entry>, Serializable {
	@Id
	@GeneratedValue(generator = "hibseq")
	@GenericGenerator(name = "hibseq", strategy = "seqhilo", parameters = { @Parameter(name = "max_lo", value = "50"), })
	Long							id;

	@NaturalId
	@ManyToOne
	@NotNull
	private Databank				databank;
	@NaturalId
	@Length(max = 10)
	@NotNull
	private String					pdbid;

	@ManyToOne(cascade = CascadeType.ALL)
	private File					file;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@Sort(type = SortType.NATURAL)
	private SortedSet<Annotation>	annotations	= new TreeSet<Annotation>();

	protected Entry() {
	}

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
