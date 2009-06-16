package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.FilePanel;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class FilesPanel extends Panel {
	public FilesPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final List<Entry> withFile = new ArrayList<Entry>();
		for (Entry ent : entrylist.getObject())
			if (ent.getFile() != null)
				withFile.add(ent);

		//Download link
		add(new ResourceLink<WebResource>("export", new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (Entry entry : withFile) {
					sb.append(entry.getDatabank().getName());
					sb.append(',');
					sb.append(entry.getPdbid());
					sb.append(',');
					String href = entry.getDatabank().getFilelink();
					href = href.replace("${PDBID}", entry.getPdbid());
					href = href.replace("${PART}", entry.getPdbid().substring(1, 3));
					sb.append(href);
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(source.replaceAll("[\\W]", "") + "_files.txt");
			}
		}.setCacheable(false)));

		//List of PDBIDs
		add(new ListView<Entry>("entrylist", withFile) {
			@Override
			protected void populateItem(ListItem<Entry> item) {
				item.add(new FilePanel("file", item.getModelObject()));
			}
		});
	}
}
