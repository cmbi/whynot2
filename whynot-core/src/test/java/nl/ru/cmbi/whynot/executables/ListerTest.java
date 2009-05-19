package nl.ru.cmbi.whynot.executables;

import nl.ru.cmbi.whynot.list.Lister;

import org.junit.Ignore;
import org.junit.Test;

public class ListerTest {
	@Test
	@Ignore
	public void DSSPWithWithoutWith() throws Exception {
		Lister.main(new String[] { "DSSP", "withFile", "withoutParentFile", "withComment" });
	}

	@Test
	@Ignore
	public void STRUCTUREFACTORSWithoutWithoutWith() throws Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withComment" });
	}

	@Test
	@Ignore
	public void STRUCTUREFACTORSWithoutWithoutWithoutThisComment() throws Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withoutComment", "_refln.status column missing" });
	}
}
