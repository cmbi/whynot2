package nl.ru.cmbi.whynot.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Path("/")
@Produces(MediaType.APPLICATION_XML)
public class WhynotImpl implements Whynot {
	@Autowired
	private DatabankDAO	databankdao;
	@Autowired
	private EntryDAO	entrydao;

	@GET
	@Path("/annotations/{databank}/{pdbid}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String annotations(@PathParam("databank") final String databank, @PathParam("pdbid") final String pdbid) {
		return StringUtils.join(getAnnotations(databank, pdbid), '\n');
	}

	@Override
	public List<String> getAnnotations(final String databank, final String pdbid) {
		Databank db = databankdao.findByName(databank);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + databank);

		Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
		List<String> annotations = new ArrayList<String>();
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

	@GET
	@Path("/entries/{databank}/{selection}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String entries(@PathParam("databank") final String databank, @PathParam("selection") final String selection) {
		return StringUtils.join(getEntries(databank, selection), '\n');
	}

	@Override
	public List<String> getEntries(final String databank, final String selection) {
		Databank db = databankdao.findByName(databank);
		if (db == null)
			throw new IllegalArgumentException("Unknown databank: " + databank);

		CollectionType collection = CollectionType.valueOf(selection.toUpperCase());

		List<Entry> entries = new ArrayList<Entry>();
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

		List<String> pdbids = new ArrayList<String>();
		for (Entry ent : entries)
			pdbids.add(ent.getPdbid());
		return pdbids;
	}
}
