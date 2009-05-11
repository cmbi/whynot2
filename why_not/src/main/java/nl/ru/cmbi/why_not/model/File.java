package nl.ru.cmbi.why_not.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
public class File implements Comparable<File> {
	@Id
	@GeneratedValue
	Long			id;

	@NaturalId
	@NotEmpty
	@Length(max = 200)
	private String	path;

	@NaturalId
	@NotNull
	private Long	timestamp;

	protected File() {
	}

	public File(java.io.File file) {
		path = file.getAbsolutePath();
		timestamp = file.lastModified();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long time) {
		timestamp = time;
	}

	@Override
	public int compareTo(File o) {
		int value = path.compareTo(o.path);
		if (value != 0)
			return value;
		return timestamp.compareTo(o.timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (path == null ? 0 : path.hashCode());
		result = prime * result + (timestamp == null ? 0 : timestamp.hashCode());
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
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		}
		else
			if (!timestamp.equals(other.timestamp))
				return false;
		return true;
	}
}
