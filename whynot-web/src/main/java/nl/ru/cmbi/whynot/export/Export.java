package nl.ru.cmbi.whynot.export;

import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class Export {
	@SpringBean
	protected EntryDAO	entrydao;

	public void doSomething() {
		WebResource export = new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				CharSequence discounts = exportEntries(new ArrayList());
				return new StringResourceStream(discounts, "text/csv");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader("entries.csv");
			}
		};
		export.setCacheable(false);
	}

	private CharSequence exportEntries(List<Entry> entries) {
		StringBuilder sb = new StringBuilder();
		for (Entry entry : entries) {
			sb.append(entry.getDatabank());
			sb.append(',');
			sb.append(entry.getPdbid());
			sb.append('\n');
		}
		return sb;
	}
}
