package nl.ru.cmbi.whynot.search;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.AnnotationPanel;
import nl.ru.cmbi.whynot.panels.FilePanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ResultsPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public ResultsPage(PageParameters parameters) {
		if (parameters.containsKey("pdbid")) {
			String[] pdbids = parameters.getStringArray("pdbid");
			RepeatingView rv = new RepeatingView("resultslist");
			for (String pdbid : pdbids)
				if (entrydao.contains(pdbid))
					rv.add(new ResultFragment(rv.newChildId(), pdbid));
				else
					warn("No data available for PDBID \"" + pdbid + "\"");
			add(rv);
		}
		else {
			error("No value specified for parameter pdbid.");
			add(new Label("resultslist"));
		}

	}

	public class ResultFragment extends Fragment {
		public ResultFragment(String id, final String pdbid) {
			super(id, "resultfragment", ResultsPage.this);
			add(new Label("pdbid", pdbid));
			ListView<Databank> lv = new ListView<Databank>("databanklist", databankdao.getAll()) {
				@Override
				protected void populateItem(ListItem<Databank> item) {
					Databank db = item.getModelObject();
					item.add(new Label("databank", db.getName()));
					Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
					if (entry != null && entry.getFile() != null)
						item.add(new FilePanel("result", entry));
					else
						if (entry != null && !entry.getAnnotations().isEmpty())
							item.add(new AnnotationPanel("result", entry));
						else
							item.add(new Label("result", ""));
				}
			};
			add(lv);
		}
	}
}
