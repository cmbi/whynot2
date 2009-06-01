package nl.ru.cmbi.whynot.list;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Lister {
	private enum SelectionType {
		VALID, OBSOLETE, MISSING, ANNOTATED, UNANNOTATED
	};

	public static void main(String... args) {
		String dbname = "DATABASE";
		String selection = "VALID|OBSOLETE|MISSING|ANNOTATED|UNANNOTATED";
		if (args.length != 2 || !args[1].matches(selection))
			throw new IllegalArgumentException("Usage: lister " + dbname + " " + selection);

		Lister lister = (Lister) SpringUtil.getContext().getBean("lister");
		lister.list(args[0], SelectionType.valueOf(args[1]));
	}

	@Autowired
	private DatabankDAO	dbdao;

	@Autowired
	private EntryDAO	entdao;

	private void list(String dbname, SelectionType selection) {
		Databank db = dbdao.findByName(dbname);
		List<Entry> entries = new ArrayList<Entry>();
		switch (selection) {// TODO
		case VALID:
			entries = entdao.getValid(db);
			break;
		case OBSOLETE:
			entries = entdao.getObsolete(db);
			break;
		case MISSING:
			entries = entdao.getMissing(db);
			break;
		case ANNOTATED:
			entries = entdao.getMissingWith(db);
			break;
		case UNANNOTATED:
			entries = entdao.getMissingWithout(db);
			break;
		}
		Logger.getLogger(getClass()).debug("#" + dbname + " " + selection + ": " + entries.size());
		for (Entry ent : entries)
			System.out.println(ent);
	}

	public static void main_old(String[] args) throws Exception {
		String dbname = "DATABASE";
		String fileFilter = "withFile|withoutFile";
		String parentFilter = "withParentFile|withoutParentFile";
		String commentFilter = "withComment|withoutComment";
		String comment = "[\"Example comment\"]";

		if (args.length != 4 && args.length != 5 || !args[1].matches(fileFilter) || !args[2].matches(parentFilter) || !args[3].matches(commentFilter))
			throw new IllegalArgumentException("Usage: lister " + dbname + " " + fileFilter + " " + parentFilter + " " + commentFilter + " " + comment);

		dbname = args[0];
		fileFilter = args[1];
		parentFilter = args[2];
		commentFilter = args[3];
		comment = null;
		if (args.length == 5)
			comment = args[4];

		Lister lister = (Lister) SpringUtil.getContext().getBean("lister");
		lister.list_old(dbname, fileFilter, parentFilter, commentFilter, comment);
	}

	public void list_old(String dbname, String fileFilter, String parentFilter, String commentFilter, String comment) throws Exception {
		Databank db = dbdao.findByExample(new Databank(dbname), "id", "reference", "filelink", "parent", "regex", "crawltype", "entries");
		if (db == null)
			new IllegalArgumentException("Databank with name " + dbname + " not found.");

		dbdao.enableFilter(fileFilter);
		dbdao.enableFilter(parentFilter);
		if (comment == null)
			dbdao.enableFilter(commentFilter);
		else
			dbdao.enableFilter(commentFilter.replace("Comment", "ThisComment"), "comment", comment);

		SortedSet<Entry> entries = db.getEntries();
		System.out.println("#" + dbname + " " + fileFilter + " " + parentFilter + " " + commentFilter + ": " + entries.size() + " entries");
		for (Entry entry : entries)
			System.out.println(entry);

		Logger.getLogger(getClass()).debug("list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " \"" + comment + "\": Succes");
	}
}
