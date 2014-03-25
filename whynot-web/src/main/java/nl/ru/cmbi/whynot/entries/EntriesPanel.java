package nl.ru.cmbi.whynot.entries;

import java.util.List;
import java.io.*;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import nl.ru.cmbi.whynot.model.Entry;

public class EntriesPanel extends Panel {
	public EntriesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Download link
		add(new Link("export-entries") {

			@Override
			public void onClick() {  
			      
				AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

		            @Override
		            public void write(OutputStream output) throws IOException {
			        
		            	Writer writer = new OutputStreamWriter(output);
						
						for (Entry entry : entrylist.getObject()) {
							writer.write(entry.toString());
							writer.write('\n');
						}
		            }
				};
				ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, source.replaceAll("[\\W]", "") + "_entries.txt");        
				getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
			}
		});

		//List of PDBIDs
		DataView<Entry> dv = new DataView<Entry>("entrylist", new ListDataProvider<Entry>(entrylist.getObject())) {
			@Override
			protected void populateItem(Item<Entry> item) {
				item.add(new Label("entry", item.getModelObject().toString()));
			}
		};
		dv.setItemsPerPage(2000);
		add(dv);
		add(new PagingNavigator("navigator", dv));
	}
}
