package nl.ru.cmbi.why_not.hibernate;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.model.Entry;

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
		entdao.enableFilter("withComment", "comment", "Another new example comment from com1.txt");
		Logger.getLogger(FilterTest.class).info("Before");
		Logger.getLogger(FilterTest.class).info(entdao.countAll());
		for (Entry entry : entdao.findAll())
			Logger.getLogger(FilterTest.class).info(entry);

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
	}
}
