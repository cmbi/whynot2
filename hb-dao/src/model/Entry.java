package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class Entry implements Serializable {
	private String		pdbid;
	@OneToOne
	private Database	database;

	protected Entry() {}
}
