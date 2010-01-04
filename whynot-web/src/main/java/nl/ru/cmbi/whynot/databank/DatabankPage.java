package nl.ru.cmbi.whynot.databank;

import java.util.Arrays;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.panels.PieChartPanel;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class DatabankPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public DatabankPage() {
		add(new AjaxLazyLoadPanel("piechart") {
			@Override
			public Component getLazyLoadComponent(String markupId) {
				return new Label(markupId, "Bla");
			}
		});
		add(databankListView(databankdao.getAll().toArray(new Databank[0])));
	}

	public DatabankPage(PageParameters parameters) {
		Databank db = null;
		if (parameters.containsKey("name")) {
			String name = parameters.getString("name");
			db = databankdao.findByName(name);
		}

		if (db != null)
			add(databankListView(db));
		else {
			error("Could not find databank for parameter name.");
			add(databankListView(databankdao.getAll().toArray(new Databank[0])));
		}
	}

	public ListView<Databank> databankListView(Databank... databanks) {
		ListView<Databank> chartlist = new ListView<Databank>("chartlist", Arrays.asList(databanks)) {
			@Override
			protected void populateItem(ListItem<Databank> item) {
				final Databank db = item.getModelObject();
				item.add(new Label("name", db.getName()));
				item.add(new AjaxLazyLoadPanel("chartpanel") {
					@Override
					public Component getLazyLoadComponent(String markupId) {
						return new PieChartPanel(markupId, db);
					}
				});
				item.add(new ExternalLink("reference", db.getReference()).add(new Label("href", db.getReference())));
			}
		};
		return chartlist;
	}
}
