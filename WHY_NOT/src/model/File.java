package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.validator.NotNull;

@Entity
public class File {
	@OneToOne
	@NotNull
	private Entry		entry;

	@ManyToOne
	@JoinColumn(insertable = false, updatable = false)
	private Databank	entry_databank; //Needed for @OneToMany Databank.files

	@Id
	protected String	path;
	@NotNull
	private Long		time;

	protected File() {}

	public File(Entry entry, String path, Long time) {
		this.entry = entry;
		entry_databank = entry.databank;
		this.path = path;
		this.time = time;
	}

	@Override
	public String toString() {
		return entry + "," + path + "," + time;
	}
}
