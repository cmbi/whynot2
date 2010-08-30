package nl.ru.cmbi.whynot.entries;

import java.util.List;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import nl.ru.cmbi.whynot.model.Entry;

public class EntriesPanel extends Panel {
	public EntriesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Download link
		add(new ResourceLink<WebResource>("export", new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (Entry entry : entrylist.getObject()) {
					sb.append(entry.toString());
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(source.replaceAll("[\\W]", "") + "_entries.txt");
			}
		}.setCacheable(false)));

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
