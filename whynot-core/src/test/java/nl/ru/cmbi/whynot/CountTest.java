package nl.ru.cmbi.whynot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.mongo.EntryRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class CountTest {
	
	@Autowired
	private EntryRepo		entdao;

	@Autowired
	private DatabankRepo	dbdao;

	private Databank		dssp;

	@Before
	public void setup() {
		dssp = dbdao.findByName("DSSP");
		Assert.assertNotNull(dssp);
	}

	@Test
	public void present() {
		long countPresent = entdao.countPresent(dssp);
		Assert.assertTrue(countPresent > 0);
	}

	@Test
	public void valid() {
		long countValid = entdao.countValid(dssp);
		Assert.assertTrue(countValid > 0);
	}

	@Test
	public void obsolete() {
		long countObsolete = entdao.countObsolete(dssp);
		Assert.assertTrue(countObsolete > 0);
	}

	@Test
	public void missing() {
		long countMissing = entdao.countMissing(dssp);
		Assert.assertTrue(countMissing > 0);
	}

	@Test
	public void annotated() {
		long countAnnotated = entdao.countAnnotated(dssp);
		Assert.assertTrue(countAnnotated > 0);
	}

	@Test
	public void unannotated() {
		long countUnannotated = entdao.countUnannotated(dssp);
		Assert.assertTrue(countUnannotated > 0);
	}
}
