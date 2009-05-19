package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Entry;

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
		//entdao.getSession().enableFilter("inDatabank").setParameter("name", "DSSP");

		//entdao.getSession().enableFilter("withFile");
		//entdao.getSession().enableFilter("withoutFile");

		//entdao.getSession().enableFilter("withParentFile");
		//entdao.getSession().enableFilter("withoutParentFile");

		//entdao.getSession().enableFilter("withComment");
		//entdao.getSession().enableFilter("withoutComment");
		//entdao.enableFilter("withComment", "comment", "Nucleic acids only");
		//entdao.enableFilter("inDatabank", "name", "DSSP");
		entdao.enableFilter("withComment", "comment", "%");
		Logger.getLogger(FilterTest.class).info("Here it comes: ");
		Logger.getLogger(FilterTest.class).info(entdao.countAll());
		for (Entry entry : entdao.findAll())
			Logger.getLogger(FilterTest.class).info(entry);

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
	}
}
