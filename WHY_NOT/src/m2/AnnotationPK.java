package m2;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class AnnotationPK implements Serializable {
	@ManyToOne
	Author	author;
	@ManyToOne
	Comment	comment;
}
