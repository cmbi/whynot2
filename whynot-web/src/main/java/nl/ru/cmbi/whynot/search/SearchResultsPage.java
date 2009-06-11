package nl.ru.cmbi.whynot.search;

import java.util.Arrays;

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
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SearchResultsPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public SearchResultsPage(PageParameters parameters) {
		if (parameters.containsKey("pdbid")) {
			String[] pdbids = parameters.getStringArray("pdbid");
			ListView<String> lv = new ListView<String>("searchresult", Arrays.asList(pdbids)) {
				@Override
				protected void populateItem(ListItem<String> item) {
					String pdbid = item.getModelObject();
					if (entrydao.contains(pdbid)) {
						item.add(new Label("pdbid", pdbid));
						item.add(new FileHierarchyFragment("filehierarchy", databankdao.findByName("PDB"), pdbid));
					}
					else
						warn("Entry " + pdbid + " could not be found.");
					//FIXME: pdbid is now declared in markup but not added in code
				}
			};
			add(lv);
		}

	}

	public class FileHierarchyFragment extends Fragment {

		public FileHierarchyFragment(String id, final Databank db, String pdbid) {
			super(id, "filehierarchyfragment", SearchResultsPage.this, new Model<Databank>(db));
			add(new Label("databank", db.getName()));
			//TODO use icons

			//File
			Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
			if (entry != null && entry.getFile() != null) {
				String href = db.getFilelink();
				href = href.replace("${PDBID}", entry.getPdbid());
				href = href.replace("${PART}", entry.getPdbid().substring(1, 3));

				String path = entry.getFile().getPath();
				path = path.substring(path.lastIndexOf('/') + 1);
				//FIXME Real link in html aswell, so move outside li and into a
				add(new ExternalLink("file", href, path));
			}
			else
				add(new Label("file").setRenderBodyOnly(true));

			//Annotations
			if (entry != null && !entry.getAnnotations().isEmpty()) {
				RepeatingView rv = new RepeatingView("annotation");
				for (Annotation ann : entry.getAnnotations())
					rv.add(new Label(rv.newChildId(), ann.getComment().getText()));
				add(rv);
			}
			else
				add(new Label("annotation").setRenderBodyOnly(true));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new FileHierarchyFragment(children.newChildId(), child, pdbid));
		}
	}
}
