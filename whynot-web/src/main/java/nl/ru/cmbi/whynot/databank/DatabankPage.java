package nl.ru.cmbi.whynot.databank;

import java.util.Arrays;

import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.mongo.EntryRepo;
import nl.ru.cmbi.whynot.panels.PieChartPanel;

import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("databanks")
public class DatabankPage extends HomePage {
	
	@SpringBean
	protected EntryRepo	entrydao;
	@SpringBean
	protected DatabankRepo	databankdao;

	public DatabankPage() {
		add(databankListView(databankdao.findAll().toArray(new Databank[0])));
	}

	public DatabankPage(final PageParameters parameters) {
		Databank db = null;
		if (parameters.getNamedKeys().contains("name")) {
			String name = parameters.get("name").toString();
			db = databankdao.findByName(name);
		}

		if (db != null)
			add(databankListView(db));
		else {
			error("Could not find databank for parameter name.");
			add(databankListView(databankdao.findAll().toArray(new Databank[0])));
		}
	}

	public ListView<Databank> databankListView(final Databank... databanks) {
		ListView<Databank> chartlist = new ListView<Databank>("chartlist", Arrays.asList(databanks)) {
			@Override
			protected void populateItem(final ListItem<Databank> item) {
				final Databank db = item.getModelObject();
				item.add(new Label("name", db.getName()));
				item.add(new AjaxLazyLoadPanel("chartpanel") {
					@Override
					public Component getLazyLoadComponent(final String markupId) {
						return new PieChartPanel(markupId, db);
					}
				});
				item.add(new ExternalLink("reference", db.getReference()).add(new Label("href", db.getReference())));
			}
		};
		return chartlist;
	}
}
