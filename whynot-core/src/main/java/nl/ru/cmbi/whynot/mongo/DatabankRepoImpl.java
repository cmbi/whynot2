package nl.ru.cmbi.whynot.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;


import nl.ru.cmbi.whynot.model.Databank;

@Service
public class DatabankRepoImpl implements DatabankRepo {
	
	@Autowired
	WhynotRepo whynotdao;
	
	private MongoCollection<Document> databanksCollection;
	
	@PostConstruct
	public void init() {
		
		databanksCollection = whynotdao.getCollection("databanks");
	}
	
	public Databank findByName(final String name) {
		
		Document doc = databanksCollection.find(Filters.eq("name",name)).first();
		if(doc==null)
			return null;
		
		return new Databank(doc);
	}
	
	public Databank getParent(Databank db)
	{
		if(db==null)
			return null;
			
		return findByName(db.getParentName());
	}

    public Databank getRoot() {
    	
    	Document doc = databanksCollection.find(Filters.exists("parent_name",false)).first();
		if(doc==null)
			return null;

        return new Databank(doc);
    }

	public List<Databank> findAll() {
		
		// Get root databank PDB
		Databank root = getRoot();
		if(root==null)
			return null;

		// Get all databanks
		List<Databank> allDatabanks = new ArrayList<Databank>(); 
		for(Document doc : databanksCollection.find()) {
			
			allDatabanks.add(new Databank(doc));
		}

		// Return databanks in hierachical order
		return getDatabanksInTreeOrder(root, allDatabanks);
	}

	public long countAll() {
		
		return databanksCollection.count();
	}

	private List<Databank> getDatabanksInTreeOrder(final Databank rootdb, final Collection<Databank> allDatabanks) {
		List<Databank> children = new ArrayList<Databank>();
		// Add root node
		children.add(rootdb);
		for (Databank child : allDatabanks)
			if (!rootdb.equals(child) && rootdb.equals(getParent(child)))
				// Recursively add child itself and grandchildren
				children.addAll(getDatabanksInTreeOrder(child, allDatabanks));
		return children;
	}
}
