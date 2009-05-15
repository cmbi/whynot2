package nl.ru.cmbi.why_not.executables;

import nl.ru.cmbi.why_not.list.Lister;

import org.junit.Test;

public class ListerTest {
	@Test
	public void DSSPWithWithoutWith() throws Exception {
		Lister.main(new String[] { "DSSP", "withFile", "withoutParentFile", "withComment" });
	}

	@Test
	public void STRUCTUREFACTORSWithoutWithoutWith() throws Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withComment" });
	}

	@Test
	public void STRUCTUREFACTORSWithoutWithoutWithoutThisComment() throws Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withoutComment", "_refln.status column missing" });
	}
}
