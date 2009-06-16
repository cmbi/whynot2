package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class EntriesPage extends HomePage {
	@SuppressWarnings("unchecked")
	public EntriesPage() {
		this("No entries selected", new Model(new ArrayList()));
	}

	public EntriesPage(final String source, final IModel<List<Entry>> entrylist) {
		add(new Label("title", source + " (" + entrylist.getObject().size() + ")"));

		List<ITab> tabs = new ArrayList<ITab>();
		tabs.add(new AbstractTab(new Model<String>("PDBIDs")) {
			@Override
			public Panel getPanel(String panelId) {
				return new PdbidPanel(panelId, source, entrylist);
			}
		});
		tabs.add(new AbstractTab(new Model<String>("Entries")) {
			@Override
			public Panel getPanel(String panelId) {
				return new EntriesPanel(panelId, source, entrylist);
			}
		});
		tabs.add(new AbstractTab(new Model<String>("Files")) {
			@Override
			public Panel getPanel(String panelId) {
				return new FilesPanel(panelId, source, entrylist);
			}
		});
		tabs.add(new AbstractTab(new Model<String>("Comments")) {
			@Override
			public Panel getPanel(String panelId) {
				return new CommentsPanel(panelId, source, entrylist);
			}
		});
		//TODO: DatabankPanel
		//TODO: TimelinePanel
		add(new TabbedPanel("tabs", tabs));
	}
}
