package nl.ru.cmbi.whynot;

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
public class DAOTest {
	@Autowired
	private DatabankRepo	dbdao;
	@Autowired
	private EntryRepo	entdao;

	private Databank	dssp;

	@Before
	public void getDatabank() {
		dssp = dbdao.findByName("DSSP");
	}

	// TODO These are pretty poor tests

	@Test
	public void getPresent() {
		int count = entdao.getPresent(dssp).size();
		System.out.println(count);
	}

	@Test
	public void getValid() {
		int count = entdao.getValid(dssp).size();
		System.out.println(count);
	}

	@Test
	public void getObsolete() {
		int count = entdao.getObsolete(dssp).size();
		System.out.println(count);
	}

	@Test
	public void getMissing() {
		int count = entdao.getMissing(dssp).size();
		System.out.println(count);
	}

	@Test
	public void getAnnotated() {
		int count = entdao.getAnnotated(dssp).size();
		System.out.println(count);
	}

	@Test
	public void getUnannotated() {
		int count = entdao.getUnannotated(dssp).size();
		System.out.println(count);
	}
}
