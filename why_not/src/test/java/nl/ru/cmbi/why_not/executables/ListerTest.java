package nl.ru.cmbi.why_not.executables;

import static org.junit.Assert.fail;
import nl.ru.cmbi.why_not.list.Lister;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.BeansException;

public class ListerTest {
	@Test
	public void injectionTest() throws BeansException, Exception {
		Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withoutComment", "_refln.status column missing" });
	}

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
	public void STRUCTUREFACTORSWithoutWithoutWithThisComment() {
		try {
			Lister.main(new String[] { "STRUCTUREFACTORS", "withoutFile", "withoutParentFile", "withoutComment", "_refln.status column missing" });
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
