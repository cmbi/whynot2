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
	public void getXXXcount() {
		Databank nrg = dbdao.findByName("DSSP");
		System.err.println(entdao.countPresent(nrg));
		System.err.println(entdao.countValid(nrg));
		System.err.println(entdao.countObsolete(nrg));
		System.err.println(entdao.countMissing(nrg));
		System.err.println(entdao.countAnnotated(nrg));
		System.err.println(entdao.counUnannotated(nrg));
	}

	@Test
	//@Ignore
	@Deprecated
	public void getXXXsize() {
		Databank nrg = dbdao.findByName("DSSP");
		System.err.println(entdao.getPresent(nrg).size());
		System.err.println(entdao.getValid(nrg).size());
		System.err.println(entdao.getObsolete(nrg).size());
		System.err.println(entdao.getMissing(nrg).size());
		System.err.println(entdao.getAnnotated(nrg).size());
		System.err.println(entdao.getUnannotated(nrg).size());
	}
}
