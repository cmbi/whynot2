package nl.ru.cmbi.whynot;

import java.io.IOException;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class CountTest {
	private static final int	factor	= 100;

	@Autowired
	private EntryDAO			entdao;

	@Autowired
	private DatabankDAO			dbdao;

	@Autowired
	private DBMock				dbMock;

	@Before
	public void setup() throws IOException {
		dbMock.setupDatabase(factor);
	}

	@Test
	@Transactional
	public void getXXXcount() {
		Databank dssp = dbdao.findByName("DSSP");

		// Check the various counts
		long countPresent = entdao.countPresent(dssp);
		long countValid = entdao.countValid(dssp);
		long countObsolete = entdao.countObsolete(dssp);
		long countMissing = entdao.countMissing(dssp);
		long countAnnotated = entdao.countAnnotated(dssp);
		long counUnannotated = entdao.counUnannotated(dssp);

		Assert.assertEquals(12 * factor, countPresent);
		Assert.assertEquals(8 * factor, countValid);
		Assert.assertEquals(4 * factor, countObsolete);
		Assert.assertEquals(2 * factor + 1, countMissing);
		Assert.assertEquals(1, countAnnotated);
		Assert.assertEquals(2 * factor, counUnannotated);

		Assert.assertEquals(countPresent, entdao.getPresent(dssp).size());
		Assert.assertEquals(countValid, entdao.getValid(dssp).size());
		Assert.assertEquals(countObsolete, entdao.getObsolete(dssp).size());
		Assert.assertEquals(countMissing, entdao.getMissing(dssp).size());
		Assert.assertEquals(countAnnotated, entdao.getAnnotated(dssp).size());
		Assert.assertEquals(counUnannotated, entdao.getUnannotated(dssp).size());
	}
}
