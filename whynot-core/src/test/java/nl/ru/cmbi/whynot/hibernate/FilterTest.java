package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.log4j.Logger;
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
public class FilterTest {
	@Autowired
	private EntryDAO	entdao;

	@Autowired
	private DatabankDAO	dbdao;

	@Test
	public void getXXXsize() {
		Databank nrg = dbdao.findByName("NRG");
		System.err.println(entdao.getValid(nrg).size());
		System.err.println(entdao.getObsolete(nrg).size());
		System.err.println(entdao.getMissing(nrg).size());
		System.err.println(entdao.getAnnotated(nrg).size());
		System.err.println(entdao.getUnannotated(nrg).size());
	}

	@Test
	public void getXXXcount() {
		Databank nrg = dbdao.findByName("NRG");
		System.err.println(entdao.getValidCount(nrg));
		System.err.println(entdao.getObsoleteCount(nrg));
		System.err.println(entdao.getMissingCount(nrg));
		System.err.println(entdao.getAnnotatedCount(nrg));
		System.err.println(entdao.getUnannotatedCount(nrg));
	}

	@Test
	@Ignore
	public void filters() {
		//entdao.enableFilter("inDatabank").setParameter("name", "DSSP");

		//entdao.enableFilter("withFile");
		//entdao.enableFilter("withoutFile");

		//entdao.enableFilter("withParentFile");
		//entdao.enableFilter("withoutParentFile");

		//entdao.enableFilter("withComment");
		//entdao.enableFilter("withoutComment");
		//entdao.enableFilter("withComment", "comment", "Nucleic acids only");

		//entdao.enableFilter("inDatabank", "name", "DSSP");
		//entdao.enableFilter("missing", "parent_id", "1");

		//sesfac.getCurrentSession().enableFilter("missing").setParameter("parent_id", 7).setParameter("child_id", 8);

		Logger.getLogger(getClass()).info("Here it comes: ");
		Logger.getLogger(getClass()).info(entdao.countAll());
		//Logger.getLogger(getClass()).info(entdao.findAll().size());

	}
}
