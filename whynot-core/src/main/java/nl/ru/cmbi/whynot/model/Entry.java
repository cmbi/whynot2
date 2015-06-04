package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;
import java.io.File;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import nl.ru.cmbi.whynot.mongo.DatabankRepo;

import org.bson.Document;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class Entry implements Comparable<Entry> {
	
	@Autowired
	private static DatabankRepo dbdao;
	
	@NotNull
	@Setter(AccessLevel.NONE)
	private Document doc;
	
	public String getDatabankName() {
		
		String key = "databank_name";
		if(doc.containsKey(key))
			return doc.get(key).toString();
		else
			return null;
	}
	
	public Databank getDatabank() {
		
		String key = "databank_name";
		if(doc.containsKey(key))
			return dbdao.findByName(doc.get(key).toString());
		else
			return null;
	}

	public String getPDBID() {
		
		String key = "pdbid";
		if(doc.containsKey(key))
			return doc.get(key).toString();
		else
			return null;
	}

	public File getFile() {
		
		String key = "filepath";
		if(doc.containsKey(key))
			return new File(doc.get(key).toString());
		else
			return null;
	}
	
	public String getComment() {
		
		String key = "comment";
		if(doc.containsKey(key))
			return doc.get(key).toString();
		else
			return null;
	}

	public Entry(Document doc) {
		
		this.doc = doc;
	}

	public Entry(final Databank db, final String id) {
		
		this.doc = new Document();
		this.doc.put("databank_name", db.getName());
		this.doc.put("pdbid", id.toLowerCase());
	}

	@Override
	public int compareTo(final Entry o) {
		int value = getDatabankName().compareTo(o.getDatabankName());
		if (value != 0)
			return value;
		return getPDBID().compareTo(o.getPDBID());
	}

	@Override
	public String toString() {
		return getDatabankName() + "," + getPDBID();
	}
}
