package model;

public class Author {
	private String name;

	private String email;

	public Author(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public String getName() {
		return this.name;
	}

	public boolean equals(Author that) {
		return this.getName().equals(that.getName())
				&& this.getEmail().equals(that.getEmail());
	}
}
