package nl.ru.cmbi.why_not.list;

import java.util.SortedSet;

import nl.ru.cmbi.why_not.hibernate.DAOFactory;
import nl.ru.cmbi.why_not.hibernate.SpringUtil;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Lister {
	public static void main(String[] args) throws Exception {
		String dbname = "DATABASE";
		String fileFilter = "withFile|withoutFile";
		String parentFilter = "withParentFile|withoutParentFile";
		String commentFilter = "withComment|withoutComment|withOlderComment";
		String comment = "[\"Example comment\"]";

		if (args.length != 4 && args.length != 5 || !args[1].matches(fileFilter) || !args[2].matches(parentFilter) || !args[3].matches(commentFilter))
			throw new IllegalArgumentException("Usage: list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " " + comment);

		dbname = args[0];
		fileFilter = args[1];
		parentFilter = args[2];
		commentFilter = args[3];
		comment = "%"; //Wildcard
		if (args.length == 5)
			comment = args[4];

		Lister lister = (Lister) SpringUtil.getContext().getBean("lister");
		lister.list(dbname, fileFilter, parentFilter, commentFilter, comment);
	}

	@Autowired
	private DAOFactory	DAOFactory;

	public void list(String dbname, String fileFilter, String parentFilter, String commentFilter, String comment) throws Exception {
		DatabankDAO dbdao = DAOFactory.getDatabankDAO();
		Databank db = dbdao.findByNaturalId(Restrictions.naturalId().set("name", dbname));
		if (db == null)
			new IllegalArgumentException("Databank with name " + dbname + " not found.");

		DAOFactory.getSession().enableFilter(fileFilter);
		DAOFactory.getSession().enableFilter(parentFilter);
		DAOFactory.getSession().enableFilter(commentFilter).setParameter("comment", comment);

		SortedSet<Entry> entries = db.getEntries();
		System.out.println("#" + dbname + " " + fileFilter + " " + parentFilter + " " + commentFilter + ": " + entries.size() + " entries");
		for (Entry entry : entries)
			System.out.println(entry + "," + (entry.getFile() != null ? entry.getFile().getTimestamp() : -1));

		Logger.getLogger(Lister.class).debug("list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " \"" + comment + "\": Succes");
	}
}
