package nl.ru.cmbi.whynot.entries;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import nl.ru.cmbi.whynot.model.Entry;

public class PdbidPanel extends Panel {
	public PdbidPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		final Set<String> pdbids = new TreeSet<String>();
		for (Entry ent : entrylist.getObject())
			pdbids.add(ent.getPdbid());

		//Download link
		add(new Link("export-pdbids") {

			@Override
			public void onClick() {
				
				AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

		            @Override
		            public void write(OutputStream output) throws IOException {
			        
		            	Writer writer = new OutputStreamWriter(output);
			        
		            	for (String pdbid : pdbids) {
		            		writer.write(pdbid);
							writer.write('\n');
		            	}
		            }
				};
		        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, source.replaceAll("[\\W]", "") + "_pdbids.txt");        
		        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
			}
		} );

		add(new Label("text", "Unique PDBIDs (" + pdbids.size() + ")"));

		//List of PDBIDs
		add(new Label("pdbids", pdbids.toString().replaceAll("[,\\[\\]]", "")));
	}
}
