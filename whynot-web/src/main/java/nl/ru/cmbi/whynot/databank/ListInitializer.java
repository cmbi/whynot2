package nl.ru.cmbi.whynot.databank;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.webservice.Whynot;

public class ListInitializer implements IInitializer {
	public ListInitializer() {
		Injector.get().inject(this);
	}

	public void destroy(Application application) {
		// TODO Auto-generated method stub
	}

	@SpringBean
	private DatabankRepo dbdao;

	public void init(Application application) {
		
		//For each databank
		for (Databank db : dbdao.findAll()) {
			String dbname = db.getName();
			//and all collection types
			for (CollectionType ct : CollectionType.values()) {
				//create a resource
				IResource resource = getResource(dbname, ct);
				//and register it with shared resources
				
				final String resourceName=dbname + '_' + ct;
				application.getSharedResources().add(resourceName,resource);
				((WebApplication)application).mountResource("/resources/list/"+resourceName, new SharedResourceReference(resourceName));
			}
		}
	}

	@SpringBean
	private Whynot	whynot;

	public IResource getResource(final String db, final CollectionType collectionType) {
		
		return new ByteArrayResource( "text/plain", null, db + '_' + collectionType ) {
		
			@Override
			protected byte[] getData(Attributes attributes) {

				List<String> entries = whynot.getEntries(db, collectionType.toString());
                StringBuilder sb = new StringBuilder();
                for (String entry : entries) {
                    sb.append(entry.toString());
                    sb.append('\n');
                }
                return sb.toString().getBytes();
			}
				
			@Override
			protected void configureResponse(ResourceResponse response, Attributes attributes) {
				super.configureResponse(response, attributes);
				
				response.disableCaching();
			}
		};
	}
}
