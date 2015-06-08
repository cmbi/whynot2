package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.panels.FilePanel;

public class FilesPanel extends Panel {
	
	@SpringBean
	private DatabankRepo dbdao;
	
	public FilesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final List<Entry> withFile = new ArrayList<Entry>();
		for (Entry ent : entrylist.getObject())
			if (ent.getFile() != null)
				withFile.add(ent);

		//Download link
        add(new ResourceLink<ByteArrayResource>("export-files", new ByteArrayResource( "text/plain", null, source.replaceAll("[\\W]", "") + "_files.txt") {
 
        	@Override
            protected byte[] getData(Attributes attributes) {
            	
                StringBuilder sb = new StringBuilder();
                for (Entry entry : withFile) {
                    sb.append(entry.toString());
                    sb.append(',');
                    String href = dbdao.findByName(entry.getDatabankName()).getFilelink();
                    href = href.replace("${PDBID}", entry.getPdbid());
                    href = href.replace("${PART}", entry.getPdbid().substring(1, 3));
                    sb.append(href);
                    sb.append('\n');
                }
                return sb.toString().getBytes();
            }

            @Override
            protected void configureResponse(ResourceResponse response, Attributes attributes) {
				super.configureResponse(response, attributes);
				
				response.disableCaching();
            }
        }));

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
