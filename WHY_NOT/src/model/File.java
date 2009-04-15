package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.NotNull;

@Entity
public class File {
	@OneToOne
	@Cascade(value = { CascadeType.SAVE_UPDATE })
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

	public String getPath() {
		return path;
	}

	public Long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return entry + "," + path + "," + time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (path == null ? 0 : path.hashCode());
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
		File other = (File) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		}
		else
			if (!path.equals(other.path))
				return false;
		return true;
	}
}
