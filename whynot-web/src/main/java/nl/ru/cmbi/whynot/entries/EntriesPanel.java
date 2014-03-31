package nl.ru.cmbi.whynot.entries;

import java.util.List;

import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;

import nl.ru.cmbi.whynot.model.Entry;

public class EntriesPanel extends Panel {
	public EntriesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);
		
		//Download link
        add(new ResourceLink<ByteArrayResource>("export-entries", new ByteArrayResource( "text/plain", null, source.replaceAll("[\\W]", "") + "_entries.txt") {
            @Override
            protected byte[] getData(Attributes attributes) {
                StringBuilder sb = new StringBuilder();
                for (Entry entry : entrylist.getObject()) {
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
        }));

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
