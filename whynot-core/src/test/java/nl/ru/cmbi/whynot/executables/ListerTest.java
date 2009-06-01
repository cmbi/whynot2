package nl.ru.cmbi.whynot.executables;

import nl.ru.cmbi.whynot.list.Lister;

import org.junit.Test;

public class ListerTest {
	@Test
	public void nrgcing() throws Exception {
		for (String dbname : new String[] { "NMR", "NRG", "NRG-DOCR", "NRG-CING" }) {
			Lister.main(new String[] { dbname, "VALID" });
			Lister.main(new String[] { dbname, "OBSOLETE" });
			Lister.main(new String[] { dbname, "MISSING" });
			Lister.main(new String[] { dbname, "ANNOTATED" });
			Lister.main(new String[] { dbname, "UNANNOTATED" });
		}
	}
}
