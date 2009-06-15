package nl.ru.cmbi.whynot.entries;

import java.util.List;

import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class EntriesPage extends HomePage {
	private String	source	= "";

	public EntriesPage(String title, IModel<List<Entry>> entrylist) {
		add(new Label("title", source = title));
		add(new ResourceLink<WebResource>("export", asResource(entrylist)));
		add(new ListView<Entry>("entrylist", entrylist) {
			@Override
			protected void populateItem(ListItem<Entry> item) {
				item.add(new Label("pdbid", item.getModelObject().getPdbid() + " "));
			}
		});
	}

	public Resource asResource(final IModel<List<Entry>> entrylist) {
		WebResource export = new WebResource() {
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
				response.setAttachmentHeader(source.replaceAll("[\\W]", "") + "_entries.txt");
			}
		};
		return export.setCacheable(false);
	}
}
