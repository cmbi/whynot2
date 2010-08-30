package nl.ru.cmbi.whynot.model;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, of = { "path", "timestamp" })
public class File extends DomainObject implements Comparable<File> {
	@NaturalId
	@NotEmpty
	@Length(max = 200)
	@Setter(AccessLevel.NONE)
	private String	path;

	@NaturalId
	@NotNull
	@Setter(AccessLevel.NONE)
	private Long	timestamp;

	protected File() {/* Hibernate requirement */}

	public File(String path, Long timestamp) {
		this.path = path;
		this.timestamp = timestamp;
	}

	public File(java.io.File file) {
		this(file.getAbsolutePath(), file.lastModified());
	}

	@Override
	public int compareTo(File o) {
		int value = path.compareTo(o.path);
		if (value != 0)
			return value;
		return timestamp.compareTo(o.timestamp);
	}
}
