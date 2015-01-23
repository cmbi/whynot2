package nl.ru.cmbi.whynot.webservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;
import nl.ru.cmbi.whynot.model.Entry;

@RestController
@RequestMapping("/webservice/")
public class WhynotRestController {
	@Autowired
	private DatabankRepo	databankdao;
	@Autowired
	private EntryRepo		entrydao;

	@RequestMapping(value = "annotations/{databank}/{pdbid}")
	public List<String> annotations(
			@PathVariable("databank") final String databank,
			@PathVariable("pdbid") final String pdbid) {
		Databank db = databankdao.findByName(databank);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + databank);

		Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
		List<String> annotations = new ArrayList<>();
		if (entry != null)
			for (Annotation ann : entry.getAnnotations())
				annotations.add(ann.getComment().getText());

		// If we still don't have anything, see what we can find out about the parent
		if (annotations.isEmpty()) {
			Databank parentDB = db.getParent();
			Entry parentEntry = entrydao.findByDatabankAndPdbid(parentDB, pdbid);
			if (parentEntry != null && parentEntry.getFile() == null) {
				annotations.add("Missing required " + parentDB.getName() + " file");
				for (Annotation ann : parentEntry.getAnnotations())
					annotations.add(ann.getComment().getText());
			}
		}

		return annotations;
	}

	@RequestMapping(value = "entries/{databank}/{selection}")
	public List<String> entries(
			@PathVariable("databank") final String databank,
			@PathVariable("selection") final String selection) {
		Databank db = databankdao.findByName(databank);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + databank);

		CollectionType collection = CollectionType.valueOf(selection.toUpperCase());
		final List<Entry> entries;
		switch (collection) {
		default:
		case PRESENT:
			entries = entrydao.getPresent(db);
			break;
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

		// Concatenate to String
		List<String> pdbids = new ArrayList<>();
		for (Entry ent : entries)
			pdbids.add(ent.getPdbid());
		return pdbids;
	}
}
