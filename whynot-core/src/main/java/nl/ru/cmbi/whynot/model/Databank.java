package nl.ru.cmbi.whynot.model;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.mongo.EntryRepo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@SuppressWarnings("unused")
public class Databank implements Serializable, Comparable<Databank> {
	public enum CollectionType {
		PRESENT, VALID, OBSOLETE, MISSING, ANNOTATED, UNANNOTATED
	}

	public enum CrawlType {
		FILE, LINE
	}
	
	@Autowired
	private static DatabankRepo dbdao;
	@Autowired
	private static EntryRepo entdao;
	

	public String getName() {
		
		String key = "name";
		if(doc.containsKey(key))
			return doc.getString(key);
		else
			return null;
	}

	public String getReference() {

		String key = "reference";
		if(doc.containsKey(key))
			return doc.getString(key);
		else
			return null;
		
	}

	public String getFilelink() {

		String key = "filelink";
		if(doc.containsKey(key))
			return doc.getString(key);
		else
			return null;
		
	}

	public String getParentName() {

		String key = "parent_name";
		if(doc.containsKey(key))
			return doc.getString(key);
		else
			return null;
	}
	
	public String getRegex() {

		String key = "regex";
		if(doc.containsKey(key))
			return doc.getString(key);
		else
			return null;
	}
	
	public CrawlType getCrawlType() {

		String key = "crawltype", value;
		if(doc.containsKey(key))
		{
			value = doc.get(key).toString();
			for(CrawlType t : CrawlType.values()) {
				if(t.toString().equals(value))
					return t;
			}
			return null;
		}
		else
			return null;
		
	}
	
	public SortedSet<Entry> getEntries() {
		
		return entdao.findByDatabankName(this.getName());
	}

	@NotNull
	@Setter(AccessLevel.NONE)
	private Document doc;
	
	public Databank(Document doc) {
		
		this.doc = doc;
	}

	public Databank(final String name, final CrawlType crawltype, final String regex, final String reference, final String filelink) {
		this(name, null, crawltype, regex, reference, filelink);
	}

	public Databank(final String name, final Databank parent, final CrawlType crawltype, final String regex, final String reference, final String filelink) {

		this.doc = new Document();
		doc.put("name", name);
		doc.put("reference", reference);
		doc.put("filelink", filelink);
		doc.put("parent_name", parent.getName());
		doc.put("regex", regex);
		doc.put("crawltype", crawltype.toString());
	}

	@Override
	public int compareTo(final Databank o) {
		if(o==null)
			return 1;

		return getName().compareTo(o.getName());
	}
}
