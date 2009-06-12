package nl.ru.cmbi.whynot.search;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SearchResultsPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public SearchResultsPage(PageParameters parameters) {
		if (parameters.containsKey("pdbid")) {
			String[] pdbids = parameters.getStringArray("pdbid");
			RepeatingView rv = new RepeatingView("searchresult");
			for (String pdbid : pdbids)
				if (entrydao.contains(pdbid))
					rv.add(new SearchResultFragment(rv.newChildId(), pdbid));
				else
					warn("No data available for PDBID \"" + pdbid + "\"");
			add(rv);
		}
		else {
			error("No value specified for parameter pdbid.");
			add(new Label("searchresult"));
		}

	}

	public class SearchResultFragment extends Fragment {
		public SearchResultFragment(String id, final String pdbid) {
			super(id, "searchresultfragment", SearchResultsPage.this);
			add(new Label("pdbid", pdbid));
			add(new EntryFragment("entry", databankdao.findByName("PDB"), pdbid));
		}
	}

	public class EntryFragment extends Fragment {
		public EntryFragment(String id, final Databank db, String pdbid) {
			super(id, "entryfragment", SearchResultsPage.this, new Model<Databank>(db));
			add(new Label("databank", db.getName()));
			//File
			Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
			if (entry != null && entry.getFile() != null) {
				String href = db.getFilelink();
				href = href.replace("${PDBID}", entry.getPdbid());
				href = href.replace("${PART}", entry.getPdbid().substring(1, 3));

				String path = entry.getFile().getPath();
				path = path.substring(path.lastIndexOf('/') + 1);

				ExternalLink el = new ExternalLink("file", href, path);
				el.add(new AttributeModifier("class", true, new Model<String>("present")));
				add(el);
			}
			else {
				Label lbl = new Label("file", "No file");
				lbl.add(new AttributeModifier("class", true, new Model<String>("missing")));
				add(lbl);
			}

			//Annotations
			if (entry != null && !entry.getAnnotations().isEmpty()) {
				RepeatingView rv = new RepeatingView("annotation");
				for (Annotation ann : entry.getAnnotations())
					rv.add(new Label(rv.newChildId(), ann.getComment().getText()));
				add(rv);
			}
			else
				add(new Label("annotation").setVisible(false));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new EntryFragment(children.newChildId(), child, pdbid));
		}
	}
}
