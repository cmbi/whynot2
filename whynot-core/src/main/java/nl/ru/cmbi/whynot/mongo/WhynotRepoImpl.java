package nl.ru.cmbi.whynot.mongo;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.Data;
import lombok.Setter;
import lombok.AccessLevel;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import nl.ru.cmbi.whynot.model.*;

@Data
@Service
public class WhynotRepoImpl implements WhynotRepo {

	@Setter(AccessLevel.NONE)
	private MongoClient mongoClient;

	@Setter(AccessLevel.NONE)
	private MongoDatabase database;
	
	@PostConstruct
	public void init() throws IOException {
		
		Properties p = new Properties();
		p.load(WhynotRepoImpl.class.getResourceAsStream("/config.properties"));
		
		mongoClient = new MongoClient(p.getProperty("mongo-host"), Integer.parseInt(p.getProperty("mongo-port")));
		database = mongoClient.getDatabase(p.getProperty("mongo-db"));
	}
	
	@PreDestroy
	public void cleanup() {
		
		mongoClient.close();
	}
	
	public MongoCollection<Document> getCollection(final String name) {
		
		return database.getCollection(name);
	}
}