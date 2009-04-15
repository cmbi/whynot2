package m2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class File {
	@OneToOne
	Entry			entry;
	@OneToOne
	@JoinColumn(insertable = false, updatable = false)
	Databank		entry_databank	= entry.databank;

	@Id
	private String	path;
	private Long	time;
}
