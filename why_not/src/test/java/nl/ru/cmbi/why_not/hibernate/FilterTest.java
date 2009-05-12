package nl.ru.cmbi.why_not.hibernate;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.model.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FilterTest {

	@Test
	public void doSomething() {
		FilterTest test = (FilterTest) SpringUtil.getContext().getBean("filterTest");
		test.printCounts();
	}

	@Autowired
	private EntryDAO	entdao;

	public void printCounts() {
		//entdao.getSession().enableFilter("inDatabank").setParameter("name", "DSSP");

		//entdao.getSession().enableFilter("withFile");
		//entdao.getSession().enableFilter("withoutFile");

		//entdao.getSession().enableFilter("withParentFile");
		//entdao.getSession().enableFilter("withoutParentFile");

		//entdao.getSession().enableFilter("withComment");
		//entdao.getSession().enableFilter("withoutComment");
		//entdao.getSession().enableFilter("withOlderComment");
		entdao.getSession().enableFilter("withComment").setParameter("comment", "Another new example comment from com1.txt");
		Logger.getLogger(FilterTest.class).info("Before");
		Logger.getLogger(FilterTest.class).info(entdao.count());
		for (Entry entry : entdao.findAll())
			Logger.getLogger(FilterTest.class).info(entry);

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
	}
}
