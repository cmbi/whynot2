package nl.ru.cmbi.whynot.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Entry.File;

@Service
public class EntryRepoImpl implements EntryRepo {
	
	@Autowired
	WhynotRepo whynotdao;
	
	private MongoCollection<Document> entriesCollection;
	
	@PostConstruct
	public void init() {
		
		entriesCollection = whynotdao.getCollection("entries");
	}
	
	public SortedSet<Entry> findByDatabankName(final String databank_name) {
		
		SortedSet<Entry> entries = new TreeSet<Entry>();
		for(Document doc : entriesCollection.find(Filters.eq("databank_name",databank_name)))
			entries.add(new Entry(doc));
		
		return entries;
	}

	public Entry findByDatabankAndPdbid(final Databank databank, final String pdbid) {
		
		Document doc = entriesCollection.find(
				Filters.and(Filters.eq("pdbid", pdbid), Filters.eq("databank_name", databank.getName()))).first();
		
		if(doc==null)
			return null;
		
		return new Entry(doc);
	}

	public boolean contains(final String pdbid) {
		
		return entriesCollection.find(Filters.eq("pdbid", pdbid)).iterator().hasNext();
	}
	
	private Bson getPresentCriteria(final String dbname) {
		
		return Filters.and(
				Filters.exists("filepath", true),
				Filters.exists("mtime", true),
				Filters.eq("databank_name", dbname));
	}
	
	private Bson getMissingCriteria(final String dbname) {
		
		return Filters.and(
				Filters.or(
					Filters.exists("filepath", false),
					Filters.exists("mtime", false)),
				Filters.eq("databank_name", dbname));
	}
	
	private Bson getAnnotatedCriteria(final String dbname) {
		
		return Filters.and(
				Filters.exists("filepath", false),
				Filters.exists("comment", true),
				Filters.exists("mtime", true),
				Filters.eq("databank_name", dbname));
	}
	
	private SortedSet<String> getParents(final Databank db)
	{
		SortedSet<String> parents = new TreeSet<String>();
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getParentName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			parents.add(doc.getString("pdbid"));
		}
		return parents;
	}

	public List<Entry> getPresent(final Databank db) {
		
		List<Entry> entries = new ArrayList<Entry>();
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))) {
			
			entries.add(new Entry(doc));
		}
		return entries;
	}

	public long countPresent(final Databank db) {
		
		return entriesCollection.count(getPresentCriteria(db.getName()));
	}

	public long countAllPresent() {

		return entriesCollection.count(Filters.and(
				Filters.exists("filepath", true),
				Filters.exists("mtime", true)));
	}

	public List<File> getRecentFiles() {
		
		List<File> files = new ArrayList<File>();
		for(Document doc : entriesCollection.find(
				Filters.and(
						Filters.exists("filepath", true),
						Filters.exists("mtime", true)))
				.sort(new BasicDBObject("mtime" , -1)).limit(10))
		{
			Entry e = new Entry(doc);
			files.add(e.getFile());
		}
		return files;
	}

	public List<Entry> getValid(final Databank db) {
		
		BasicDBObject fields = new BasicDBObject();
		fields.put("pdbid", 1);
		
		SortedSet<String> parents = getParents(db);
		
		// Fill valid list for the root databank
		boolean isRoot = (db.getParentName()==null);
		
		List<Entry> children = new ArrayList<Entry>();
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))) {
			
			Entry e = new Entry(doc);
			if(isRoot || parents.contains(e.getPdbid()))
				children.add(e);
		}
		
		return children;
	}

	public long countValid(final Databank db) {
		
		BasicDBObject fields = new BasicDBObject();
		fields.put("pdbid", 1);
		
		SortedSet<String> parents = getParents(db);
		
		// Fill valid list for the root databank
		boolean isRoot = (db.getParentName()==null);
		
		int count = 0;
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			if(isRoot || parents.contains(doc.getString("pdbid")))
				count ++;
		}
		
		return count;
	}

	public List<Entry> getObsolete(final Databank db) {

		List<Entry> children = new ArrayList<Entry>();
		
		// Empty obsolete list for the root databank
		if(db.getParentName()==null)
			return children;
		
		BasicDBObject fields = new BasicDBObject();
		fields.put("pdbid", 1);

		SortedSet<String> parents = getParents(db);
		
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))) {
			
			Entry e = new Entry(doc);
			if(!parents.contains(e.getPdbid()))
				children.add(e);
		}
		
		return children;
	}

	public long countObsolete(final Databank db) {
		
		// Empty obsolete list for the root databank
		if(db.getParentName()==null)
			return 0;
		
		BasicDBObject fields = new BasicDBObject();
		fields.put("pdbid", 1);
		
		SortedSet<String> parents = getParents(db);
		
		long count = 0;
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			if(!parents.contains(doc.getString("pdbid")))
				count ++;
		}
		
		return count;
	}

	public List<Entry> getAnnotated(final Databank db) {
		
		List<Entry> entries = new ArrayList<Entry>();
		for(Document doc : entriesCollection.find(getAnnotatedCriteria(db.getName()))) {
			
			entries.add(new Entry(doc));
		}
		return entries;
	}
	
	public long countAnnotated(final Databank db) {
		
		return entriesCollection.count(getAnnotatedCriteria(db.getName()));
	}

	public long countAllAnnotated() {
		
		return entriesCollection.count(
						Filters.and(
						Filters.exists("filepath", false),
						Filters.exists("mtime", true),
						Filters.exists("comment", true)));
	}

	public List<Entry> getMissing(final Databank db) {
		
		SortedSet<String> present = new TreeSet<String>();
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			present.add(doc.getString("pdbid"));
		}
		Map<String,Entry> missing = new HashMap<String,Entry>();
		for(Document doc : entriesCollection.find(getMissingCriteria(db.getName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			Entry e = new Entry(doc);
			missing.put(e.getPdbid(), e);
		}
		
		List<Entry> entries = new ArrayList<Entry>();
		for(String parent : getParents(db)) {
			if(!present.contains(parent))
			{
				if(missing.containsKey(parent))
					entries.add(missing.get(parent));
				else
					entries.add(new Entry(db,parent));
			}
		}
		return entries;
	}

	public long countMissing(final Databank db) {

		SortedSet<String> present = new TreeSet<String>();
		for(Document doc : entriesCollection.find(getPresentCriteria(db.getName()))
				.projection(new BasicDBObject("pdbid",1))) {
			
			present.add(doc.getString("pdbid"));
		}
		
		long count = 0;
		for(String parent : getParents(db)) {
			if(!present.contains(parent))
			{
				count ++;
			}
		}
		return count;
	}

	public List<Entry> getUnannotated(final Databank db) {
		
		List<Entry> entries = new ArrayList<Entry>();
		for(Entry e : getMissing(db)) { 
			if(e.getComment() == null)
				entries.add(e);
		}
		return entries;
	}

	public long countUnannotated(final Databank db) {
		
		long count = 0;
		for(Entry e : getMissing(db)) { 
			if(e.getComment() == null)
				count ++;
		}
		return count;
	}
	
	public List<String> listComments()
	{
		List<String> comments = new ArrayList<String>();
		for(String s : entriesCollection.distinct("comment", String.class).filter(Filters.ne("comment", null)))
		{
			comments.add(s);
		}
		return comments;
	}
	
	public long getLastAnnotation(final String comment)
	{
		Document doc = entriesCollection.find(
				Filters.and(Filters.eq("comment", comment), Filters.exists("mtime")))
				.projection(new BasicDBObject( "mtime" , 1 ))
				.sort(new BasicDBObject( "mtime" , -1 )).first();
		
		if(doc==null)
			return 0;
		
		// convert seconds to milliseconds
		return Math.round(1000 * doc.getDouble("mtime"));
	}

	public List<Entry> getRecentlyAnnotated() {
		
		List<Entry> annotations = new ArrayList<Entry>();
		for(Document doc : entriesCollection.find(
				Filters.and(
					Filters.exists("comment", true),
					Filters.exists("mtime", true)))
			.sort(new BasicDBObject("mtime" , -1)).limit(10))
		{
			annotations.add(new Entry(doc));
		}
		return annotations;
	}
	
	public List<Entry> findWithComment(final String comment)
	{
		List<Entry> entries = new ArrayList<Entry>();
		for(Document doc : entriesCollection.find(Filters.eq("comment", comment))) {
			
			entries.add(new Entry(doc));
		}
		return entries;
		
	}
	public long countWithComment(final String comment)
	{
		return entriesCollection.count(Filters.eq("comment", comment));
	}

	public long countAll() {
		
		return entriesCollection.count();
	}

	public long countComments() {
		
		return entriesCollection.count(Filters.exists("comment", true));
	}
}
