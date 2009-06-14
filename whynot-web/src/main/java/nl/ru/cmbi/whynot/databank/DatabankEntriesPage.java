package nl.ru.cmbi.whynot.databank;

import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class DatabankEntriesPage extends HomePage {
	private enum Selection {
		Obsolete, Valid, Missing, Annotated, Unannotated
	};

	@SpringBean
	protected EntryDAO	entrydao;

	private Databank	databank;
	private Selection	selection;

	public DatabankEntriesPage(PageParameters parameters) {
		if (parameters.containsKey("name")) {
			String name = parameters.getString("name");
			databank = databankdao.findByName(name);
		}
		else
			error("No value specified for parameter databank.");

		if (parameters.containsKey("selection"))
			selection = Selection.valueOf(parameters.getString("selection"));
		else
			error("No value specified for parameter selection.");

		List<Entry> entries;
		switch (selection) {
		case Obsolete:
			entries = entrydao.getObsolete(databank);
			break;
		case Valid:
			entries = entrydao.getValid(databank);
			break;
		case Missing:
			entries = entrydao.getMissing(databank);
			break;
		case Annotated:
			entries = entrydao.getAnnotated(databank);
			break;
		case Unannotated:
			entries = entrydao.getUnannotated(databank);
			break;
		default:
			entries = new ArrayList<Entry>();
		}

		add(new Label("title", selection + " " + databank.getName() + " (" + entries.size() + ")"));
		add(new ResourceLink<WebResource>("export", getEntriesResource(entries)));
		RepeatingView rv = new RepeatingView("entrylist");
		for (Entry entry : entries)
			rv.add(new Label(rv.newChildId(), entry.getPdbid() + " ").setRenderBodyOnly(true));
		add(rv);
	}

	public Resource getEntriesResource(final List<Entry> entries) {
		WebResource export = new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (Entry entry : entries) {
					sb.append(entry.getPdbid());
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(databank.getName() + "_" + selection + ".txt");
			}
		};
		return export.setCacheable(false);
	}
}
