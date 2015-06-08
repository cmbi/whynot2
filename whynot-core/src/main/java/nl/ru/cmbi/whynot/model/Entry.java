package nl.ru.cmbi.whynot.model;

import java.util.SortedSet;
import java.util.TreeSet;
import java.io.Serializable;

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
public class Entry implements Serializable, Comparable<Entry> {
	
	@Data
	public class File {

		@Setter(AccessLevel.NONE)
		private String path;
		
		@Setter(AccessLevel.NONE)
		private long mtime;
		
		public File(String path, long mtime) {
			
			this.path = path;
			this.mtime = mtime;
		}
	}
	
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

	public String getPdbid() {
		
		String key = "pdbid";
		if(doc.containsKey(key))
			return doc.get(key).toString();
		else
			return null;
	}
	
	public long getLastModified() {

		String key = "mtime";
		if(doc.containsKey(key))
			// convert seconds to milliseconds
			return Math.round(1000 * doc.getDouble(key));
		else
			return 0;
	}

	public File getFile() {
		
		if(doc.containsKey("filepath") && doc.containsKey("mtime"))
		{
			// convert seconds to milliseconds
			File f = new File(doc.get("filepath").toString(), Math.round(1000 * doc.getDouble("mtime")));
			return f;
		}
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
		return getPdbid().compareTo(o.getPdbid());
	}

	@Override
	public String toString() {
		return getDatabankName() + "," + getPdbid();
	}
}
