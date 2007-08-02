package model;

import java.io.File;
import java.util.Date;

/**
 * This class is a generalization of files containing a Molecular Data
 * Structures. It provides the ability to get the properties of this file, to
 * get a textual representation of this file and to compare this file to a given
 * other file of the same type.
 */
public class Entry extends PDBID {
	private String filepath;

	private long timestamp;

	private Database database;

	/**
	 * Used by FilesTable & PDBFinderFileReader
	 */
	public Entry(String pdbid, String filepath, long timestamp,
			Database database) {
		super(pdbid);
		this.filepath = filepath;
		this.timestamp = timestamp;
		this.database = database;
	}

	/**
	 * Used by Crawler
	 */
	public Entry(File file, Database database) {
		this(database.extractPDBID(file), file.getAbsolutePath(), file
				.lastModified(), database);
	}

	public String getFilepath() {
		return this.filepath;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public Date getDatetimestamp() {
		return new Date(this.timestamp);
	}

	public Database getDatabase() {
		return this.database;
	}

	@Override
	public String toString() {
		return this.getPDBID() + "," + this.getDatabase().getName() + ","
				+ this.getTimestamp() + "," + this.getFilepath();
	}

	public boolean equals(Entry that) {
		return super.equals(that)
				&& this.getFilepath().equals(that.getFilepath())
				&& this.getTimestamp() == that.getTimestamp()
				&& this.getDatabase().equals(that.getDatabase());
	}
}
