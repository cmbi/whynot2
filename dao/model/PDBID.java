package model;

/**
 * This class represents the abstract Molecular Structure Data identified by a
 * msdid.
 */
public class PDBID {
	private String pdbid;

	public PDBID(String pdbid) {
		this.pdbid = pdbid.toLowerCase();
	}

	public String getPDBID() {
		return this.pdbid;
	}

	public boolean equals(PDBID that) {
		return this.getPDBID().equals(that.getPDBID());
	}
}
