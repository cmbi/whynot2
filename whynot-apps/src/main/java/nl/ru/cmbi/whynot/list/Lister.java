package nl.ru.cmbi.whynot.list;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.util.SpringUtil;

@Service
@Transactional
public class Lister {
	private static final Logger	log	= LoggerFactory.getLogger(Lister.class);

	public static void main(String... args) {
		String dbname = "DATABASE";
		String selection = "PRESENT|VALID|OBSOLETE|MISSING|ANNOTATED|UNANNOTATED";
		if (args.length != 2 || !args[1].matches(selection))
			throw new IllegalArgumentException("Usage: lister " + dbname + " " + selection);

		Lister lister = SpringUtil.getContext().getBean(Lister.class);
		lister.list(args[0], CollectionType.valueOf(args[1]));
	}

	@Autowired
	private DatabankDAO	dbdao;

	@Autowired
	private EntryDAO	entdao;

	public void list(String dbname, CollectionType selection) {
		Databank db = dbdao.findByName(dbname);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + dbname);

		List<Entry> entries = new ArrayList<Entry>();
		switch (selection) {
		case PRESENT:
			entries = entdao.getPresent(db);
			break;
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
			entries = entdao.getAnnotated(db);
			break;
		case UNANNOTATED:
			entries = entdao.getUnannotated(db);
			break;
		}
		log.info("#" + dbname + " " + selection + ": " + entries.size());
		for (Entry ent : entries)
			System.out.println(ent.getPdbid());
	}
}
