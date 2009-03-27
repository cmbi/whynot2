package model;

public class Property {
	private String name;

	private String explanation;

	public Property(String name, String explanation) {
		this.name = name;
		this.explanation = explanation;
	}

	public boolean equals(Property that) {
		return this.getName().equals(that.getName())
				&& this.getExplanation().equals(that.getExplanation());
	}

	public String getName() {
		return this.name;
	}

	public String getExplanation() {
		return this.explanation;
	}
}
