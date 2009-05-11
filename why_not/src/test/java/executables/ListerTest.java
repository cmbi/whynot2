package executables;

import list.Lister;

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
	public void STRUCTUREFACTORSWithoutWithoutWithThisComment() throws Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withoutComment", "_refln.status column missing" });
	}
}
