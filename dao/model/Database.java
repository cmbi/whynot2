package model;

import java.io.File;
import java.io.FileFilter;

public class Database implements FileFilter {
	private String name;

	private String regex;

	private int offset;

	private String reference = null;

	private String filelink = null;

	public Database(String name, String regex, int offset) {
		this.name = name.toUpperCase();
		this.regex = regex;
		this.offset = offset;
	}

	public boolean accept(File file) {
		return this.accept(file.getAbsolutePath());
	}

	public String extractPDBID(File file) {
		return file.getName().substring(this.offset, this.offset + 4);
	}

	public boolean accept(String line) {
		return line.matches(this.regex);
	}

	public boolean equals(Database that) {
		return that != null && this.getName().equals(that.getName())
				&& this.getRegex().equals(that.getRegex())
				&& this.getOffset() == that.getOffset();
	}

	public String getName() {
		return this.name;
	}

	public int getOffset() {
		return this.offset;
	}

	public String getRegex() {
		return this.regex;
	}

	public String getFilelink() {
		return this.filelink;
	}

	public void setFilelink(String filelink) {
		this.filelink = filelink;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
