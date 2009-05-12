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
	private DAOFactory	factory;

	public void printCounts() {
		//factory.getSession().enableFilter("inDatabank").setParameter("name", "DSSP");

		//factory.getSession().enableFilter("withFile");
		//factory.getSession().enableFilter("withoutFile");

		//factory.getSession().enableFilter("withParentFile");
		//factory.getSession().enableFilter("withoutParentFile");

		//factory.getSession().enableFilter("withComment");
		//factory.getSession().enableFilter("withoutComment");
		//factory.getSession().enableFilter("withOlderComment");
		factory.getSession().enableFilter("withComment").setParameter("comment", "Another new example comment from com1.txt");

		EntryDAO entdao = factory.getEntryDAO();
		Logger.getLogger(FilterTest.class).info("Before");
		Logger.getLogger(FilterTest.class).info(entdao.count());
		for (Entry entry : entdao.findAll())
			Logger.getLogger(FilterTest.class).info(entry);

		//System.out.println(dbdao.getEntries(db, AnnotationType.ALL).size());
	}
}
