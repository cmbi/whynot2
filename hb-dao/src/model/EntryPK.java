package model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne(cascade = CascadeType.ALL)
	private Database	database;

	private String		pdbid;

	protected EntryPK() {}
}
