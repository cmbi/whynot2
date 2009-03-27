package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "tbl_authors", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Author {
	@Id
	private String	name;

	//private Set<Comment>	comments;

	public Author() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
