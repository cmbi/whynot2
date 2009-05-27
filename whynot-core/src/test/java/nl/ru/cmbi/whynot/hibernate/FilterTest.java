package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;

import org.apache.log4j.Logger;
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

	@Test
	public void printCounts() {
		//entdao.enableFilter("inDatabank").setParameter("name", "DSSP");

		//entdao.enableFilter("withFile");
		//entdao.enableFilter("withoutFile");

		//entdao.enableFilter("withParentFile");
		//entdao.enableFilter("withoutParentFile");

		//entdao.enableFilter("withComment");
		//entdao.enableFilter("withoutComment");
		//entdao.enableFilter("withComment", "comment", "Nucleic acids only");

		//entdao.enableFilter("inDatabank", "name", "DSSP");
		entdao.enableFilter("withoutThisComment", "comment", "C-aplha only");

		Logger.getLogger(getClass()).info("Here it comes: ");
		Logger.getLogger(getClass()).info(entdao.findAll().size());

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
	}
}
