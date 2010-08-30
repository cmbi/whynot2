package nl.ru.cmbi.whynot.entries;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import nl.ru.cmbi.whynot.model.Entry;

public class PdbidPanel extends Panel {
	public PdbidPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		final Set<String> pdbids = new TreeSet<String>();
		for (Entry ent : entrylist.getObject())
			pdbids.add(ent.getPdbid());

		//Download link
		add(new ResourceLink<WebResource>("export", new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (String pdbid : pdbids) {
					sb.append(pdbid);
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

		add(new Label("text", "Unique PDBIDs (" + pdbids.size() + ")"));

		//List of PDBIDs
		add(new Label("pdbids", pdbids.toString().replaceAll("[,\\[\\]]", "")));
	}
}
