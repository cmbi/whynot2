package nl.ru.cmbi.whynot.search;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
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
			ListView<Databank> lv = new ListView<Databank>("databanklist", databankdao.findAll()) {
				@Override
				protected void populateItem(ListItem<Databank> item) {
					Databank db = item.getModelObject();
					item.add(new Label("databank", db.getName()));
					Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
					if (entry != null && entry.getFile() != null)
						item.add(new FileFragment("result", entry));
					else
						if (entry != null && !entry.getAnnotations().isEmpty())
							item.add(new AnnotationFragment("result", entry));
						else
							item.add(new Label("result", "No data"));
				}
			};
			add(lv);
		}
	}

	public class AnnotationFragment extends Fragment {
		public AnnotationFragment(String id, Entry entry) {
			super(id, "annotationfragment", ResultsPage.this);
			RepeatingView rv = new RepeatingView("annotation");
			for (Annotation ann : entry.getAnnotations())
				rv.add(new Label(rv.newChildId(), ann.getComment().getText()));
			add(rv);
		}
	}

	public class FileFragment extends Fragment {
		public FileFragment(String string, Entry entry) {
			super(string, "filefragment", ResultsPage.this);
			String href = entry.getDatabank().getFilelink();
			href = href.replace("${PDBID}", entry.getPdbid());
			href = href.replace("${PART}", entry.getPdbid().substring(1, 3));

			add(new ExternalLink("file", href, "File present"));
		}
	}
}
