package nl.ru.cmbi.whynot.databank;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.Resource;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;
import nl.ru.cmbi.whynot.webservice.Whynot;

public class ListInitializer implements IInitializer {
	public ListInitializer() {
		InjectorHolder.getInjector().inject(this);
	}

	@SpringBean
	private DatabankDAO	dbdao;

	@Override
	public void init(Application application) {
		//For each databank
		for (Databank db : dbdao.getAll()) {
			String dbname = db.getName();
			//and all collection types
			for (CollectionType ct : CollectionType.values()) {
				//create a resource
				Resource resource = getResource(dbname, ct);
				//and register it with shared resources
				application.getSharedResources().add(this.getClass(), dbname + '_' + ct, null, null, resource);
			}
		}
	}

	@SpringBean
	private Whynot	whynot;

	public Resource getResource(final String db, final CollectionType collectionType) {
		return new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				List<String> entries = whynot.getEntries(db, collectionType.toString());
				StringBuilder sb = new StringBuilder();
				for (String entry : entries) {
					sb.append(entry.toString());
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(db + '_' + collectionType);
			}
		}.setCacheable(false);
	}
}
