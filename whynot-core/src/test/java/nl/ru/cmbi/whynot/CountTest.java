package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
@Transactional
public class CountTest {
	@Autowired
	private EntryDAO	entdao;

	@Autowired
	private DatabankDAO	dbdao;

	@Test
	@Ignore
	public void getCounts() {
		System.err.println(dbdao.countAll());
		System.err.println(entdao.countAll());
	}

	@Test
	public void getPresent() {
		Databank nrg = dbdao.findByName("NRG-CING");
		System.err.println(entdao.getPresent(nrg));
	}

	@Test
	//@Ignore
	public void getXXXsize() {
		Databank nrg = dbdao.findByName("NRG-CING");
		System.err.println(entdao.getValid(nrg).size());
		System.err.println(entdao.getObsolete(nrg).size());
		System.err.println(entdao.getMissing(nrg).size());
		System.err.println(entdao.getAnnotated(nrg).size());
		System.err.println(entdao.getUnannotated(nrg).size());
	}

	@Test
	@Ignore
	public void getXXXcount() {
		Databank nrg = dbdao.findByName("NRG-CING");
		System.err.println(entdao.getValidCount(nrg));
		System.err.println(entdao.getObsoleteCount(nrg));
		System.err.println(entdao.getMissingCount(nrg));
		System.err.println(entdao.getAnnotatedCount(nrg));
		System.err.println(entdao.getUnannotatedCount(nrg));
	}
}
