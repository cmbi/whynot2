package nl.ru.cmbi.whynot.panels;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;

public class FilePanel extends Panel {
	
	@SpringBean
	private DatabankRepo dbdao;
	
	public FilePanel(String id, Entry entry) {
		super(id);
		String href = dbdao.findByName(entry.getDatabankName()).getFilelink();
		href = href.replace("${PDBID}", entry.getPdbid());
		href = href.replace("${PART}", entry.getPdbid().substring(1, 3));
		add(new ExternalLink("file", href, href.substring(href.lastIndexOf('/') + 1)));
	}
}
