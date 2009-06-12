package nl.ru.cmbi.whynot.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@WebService(endpointInterface = "nl.ru.cmbi.whynot.webservice.Whynot")
public class WhynotImpl implements Whynot {
	@Autowired
	private DatabankDAO	databankdao;
	@Autowired
	private EntryDAO	entrydao;

	@Override
	public List<String> getAnnotations(String databank, String pdbid) {
		Databank db = databankdao.findByName(databank);
		Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
		List<String> annotations = new ArrayList<String>();
		if (entry != null && !entry.getAnnotations().isEmpty())
			for (Annotation ann : entry.getAnnotations())
				annotations.add(ann.getComment().getText());
		return annotations;
	}

	@Override
	public List<String> getEntries(String dbname, CollectionType selection) {
		Databank db = databankdao.findByName(dbname);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + dbname);

		List<Entry> entries = new ArrayList<Entry>();
		switch (selection) {
		case VALID:
			entries = entrydao.getValid(db);
			break;
		case OBSOLETE:
			entries = entrydao.getObsolete(db);
			break;
		case MISSING:
			entries = entrydao.getMissing(db);
			break;
		case ANNOTATED:
			entries = entrydao.getAnnotated(db);
			break;
		case UNANNOTATED:
			entries = entrydao.getUnannotated(db);
			break;
		}
		List<String> pdbids = new ArrayList<String>();
		for (Entry ent : entries)
			pdbids.add(ent.getPdbid());
		return pdbids;
	}
}
