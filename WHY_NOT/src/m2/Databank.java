package m2;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class Databank {
	public enum CrawlType {
		FILE, LINE
	};

	@Id
	@NotEmpty
	@Length(max = 50)
	private String		name;

	@NotEmpty
	@Length(max = 200)
	private String		reference;
	@NotEmpty
	@Length(max = 200)
	private String		filelink;

	@OneToOne
	private Databank	parent;

	@NotEmpty
	@Length(max = 50)
	private String		regex;

	@NotNull
	@Enumerated(EnumType.STRING)
	private CrawlType	crawltype;

	@OneToMany(mappedBy = "entry_databank")
	private Set<File>	files	= new HashSet<File>();

	//	@OneToMany(mappedBy = "databank", cascade = CascadeType.ALL)
	//	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	//	@Where(clause = "path != null")
	//	private Set<Entry>	entries	= new HashSet<Entry>();
}
