package m2;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class EntryPK implements Serializable {
	@ManyToOne
	Databank	databank;
	String		pdbid;
}
