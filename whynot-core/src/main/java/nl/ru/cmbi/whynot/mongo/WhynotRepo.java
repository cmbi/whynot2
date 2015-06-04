package nl.ru.cmbi.whynot.mongo;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public interface WhynotRepo {
	
	public MongoCollection<Document> getCollection(final String name);
}