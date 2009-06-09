package nl.ru.cmbi.whynot.search;

import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SearchResultsPage extends HomePage {

	public SearchResultsPage(PageParameters parameters) {
		if (parameters.containsKey("pdbid")) {
			String[] pdbids = parameters.getStringArray("pdbid");
			RepeatingView rv = new RepeatingView("searchresult");
			for (String id : pdbids)
				rv.add(new FileHierarchyFragment(rv.newChildId(), databankdao.findByName("PDB"), id));
			add(rv);
		}

	}

	public class FileHierarchyFragment extends Fragment {
		@SpringBean
		protected EntryDAO	entrydao;

		public FileHierarchyFragment(String id, final Databank db, String pdbid) {
			super(id, "hierarchyfragment", SearchResultsPage.this, new Model<Databank>(db));
			//TODO: Handle non-found entries
			Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);

			//Link
			PageParameters pp = new PageParameters();
			pp.put("name", db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(new Label("name", db.getName())));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new FileHierarchyFragment(children.newChildId(), child, pdbid));
		}
	}
}
