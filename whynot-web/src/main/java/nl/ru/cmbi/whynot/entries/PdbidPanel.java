package nl.ru.cmbi.whynot.entries;

import java.util.List;

import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class PdbidPanel extends Panel {
	public PdbidPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Download link
		add(new ResourceLink<WebResource>("export", new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (Entry entry : entrylist.getObject()) {
					sb.append(entry.getPdbid());
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(source.replaceAll("[\\W]", "") + "_pdbids.txt");
			}
		}.setCacheable(false)));

		//List of PDBIDs
		add(new ListView<Entry>("entrylist", entrylist) {
			@Override
			protected void populateItem(ListItem<Entry> item) {
				item.add(new Label("pdbid", item.getModelObject().getPdbid()));
			}
		});
	}
}
