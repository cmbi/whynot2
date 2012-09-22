package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
@Transactional
public class DAOTest {
	@Autowired
	private DatabankDAO	dbdao;
	@Autowired
	private EntryDAO	entdao;

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
