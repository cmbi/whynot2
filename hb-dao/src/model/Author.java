package model;

import javax.persistence.*;

@Entity
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
