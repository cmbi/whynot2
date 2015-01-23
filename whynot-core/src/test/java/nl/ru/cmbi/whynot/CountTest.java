package nl.ru.cmbi.whynot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.model.Databank;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WhynotApplication.class)
@Transactional
public class CountTest {
	@Autowired
	private EntryRepo		entdao;

	@Autowired
	private DatabankRepo	dbdao;

	private Databank		dssp;

	@Before
	public void setup() {
		dssp = dbdao.findByName("DSSP");
	}

	@Test
	public void present() {
		long countPresent = entdao.countPresent(dssp);
		Assert.assertEquals(12, countPresent);
		Assert.assertEquals(countPresent, entdao.getPresent(dssp).size());
	}

	@Test
	public void valid() {
		long countValid = entdao.countValid(dssp);
		Assert.assertEquals(8, countValid);
		Assert.assertEquals(countValid, entdao.getValid(dssp).size());
	}

	@Test
	public void obsolete() {
		long countObsolete = entdao.countObsolete(dssp);
		Assert.assertEquals(4, countObsolete);
		Assert.assertEquals(countObsolete, entdao.getObsolete(dssp).size());
	}

	@Test
	public void missing() {
		long countMissing = entdao.countMissing(dssp);
		Assert.assertEquals(3, countMissing);
		Assert.assertEquals(countMissing, entdao.getMissing(dssp).size());
	}

	@Test
	public void annotated() {
		long countAnnotated = entdao.countAnnotated(dssp);
		Assert.assertEquals(1, countAnnotated);
		Assert.assertEquals(countAnnotated, entdao.getAnnotated(dssp).size());
	}

	@Test
	public void unannotated() {
		long counUnannotated = entdao.counUnannotated(dssp);
		Assert.assertEquals(2, counUnannotated);
		Assert.assertEquals(counUnannotated, entdao.getUnannotated(dssp).size());
	}
}
