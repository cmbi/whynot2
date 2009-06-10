package nl.ru.cmbi.whynot.search;

import java.util.Arrays;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SearchResultsPage extends HomePage {

	public SearchResultsPage(PageParameters parameters) {
		if (parameters.containsKey("pdbid")) {
			String[] pdbids = parameters.getStringArray("pdbid");
			ListView<String> lv = new ListView<String>("searchresult", Arrays.asList(pdbids)) {
				@Override
				protected void populateItem(ListItem<String> item) {
					String id = item.getModelObject();
					item.add(new Label("pdbid", id));
					item.add(new FileHierarchyFragment("filehierarchy", databankdao.findByName("PDB"), id));
				}
			};
			add(lv);
		}

	}

	public class FileHierarchyFragment extends Fragment {
		@SpringBean
		protected EntryDAO	entrydao;

		public FileHierarchyFragment(String id, final Databank db, String pdbid) {
			super(id, "filehierarchyfragment", SearchResultsPage.this, new Model<Databank>(db));
			//TODO: Handle non-found entries
			Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
			add(new Label("databank", db.getName()));

			if (entry == null) {
				add(new Label("file", "entry == null"));
				add(new Label("annotations", "entry == null"));
			}
			else {
				if (entry.getFile() == null)
					add(new Label("file", "file == null"));
				else
					add(new Label("file", "file exists!"));

				if (entry.getAnnotations().isEmpty())
					add(new Label("annotations", "no annotations"));
				else
					add(new Label("annotations", "annotations exist!"));
			}
			/*			//Link
						PageParameters pp = new PageParameters();
						pp.put("name", db.getName());
						BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
						add(bpl.add(new Label("name", db.getName())));*/
			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new FileHierarchyFragment(children.newChildId(), child, pdbid));
		}
	}
}
