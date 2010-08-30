package nl.ru.cmbi.whynot.panels;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

import nl.ru.cmbi.whynot.model.Entry;

public class FilePanel extends Panel {
	public FilePanel(String id, Entry entry) {
		super(id);
		String href = entry.getDatabank().getFilelink();
		href = href.replace("${PDBID}", entry.getPdbid());
		href = href.replace("${PART}", entry.getPdbid().substring(1, 3));
		add(new ExternalLink("file", href, href.substring(href.lastIndexOf('/') + 1)));
	}
}
