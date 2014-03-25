package nl.ru.cmbi.whynot.entries;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.wicket.request.resource.AbstractResource;

import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.FilePanel;

public class FilesPanel extends Panel {
	public FilesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final List<Entry> withFile = new ArrayList<Entry>();
		for (Entry ent : entrylist.getObject())
			if (ent.getFile() != null)
				withFile.add(ent);

		add(new Link("export-files")
		{
			@Override
			public void onClick() {
				AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

		            @Override
		            public void write(OutputStream output) throws IOException {
				        
				        Writer writer = new OutputStreamWriter(output);
				        
						for (Entry entry : withFile) {
							writer.write(entry.toString());
							writer.write(',');
							String href = entry.getDatabank().getFilelink();
							href = href.replace("${PDBID}", entry.getPdbid());
							href = href.replace("${PART}", entry.getPdbid().substring(1, 3));
							writer.write(href);
							writer.write('\n');
						}
		            }
		        };
		        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, source.replaceAll("[\\W]", "") + "_files.txt");        
		        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
			}
		});

		add(new Label("text", "Files (" + withFile.size() + ")"));

		//List of PDBIDs
		DataView<Entry> dv = new DataView<Entry>("entrylist", new ListDataProvider<Entry>(withFile)) {
			@Override
			protected void populateItem(Item<Entry> item) {
				item.add(new FilePanel("file", item.getModelObject()));
			}
		};
		dv.setItemsPerPage(2000);
		add(dv);
		add(new PagingNavigator("navigator", dv));
	}
}
