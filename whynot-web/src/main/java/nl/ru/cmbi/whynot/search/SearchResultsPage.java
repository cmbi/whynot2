package nl.ru.cmbi.whynot.search;

import java.util.Arrays;
import java.util.Collection;

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
			add(new Label("databank", db.getName()));
			/*//Link
			PageParameters pp = new PageParameters();
			pp.put("name", db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(new Label("name", db.getName())));*/

			Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
			if (entry != null && entry.getFile() != null)
				add(new FileFragment("file", db, entry));
			else
				add(new Label("file"));
			if (entry != null && !entry.getAnnotations().isEmpty())
				add(new AnnotationFragment("annotations", entry.getAnnotations()));
			else
				add(new Label("annotations"));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new FileHierarchyFragment(children.newChildId(), child, pdbid));
		}
	}

	public class AnnotationFragment extends Fragment {
		public AnnotationFragment(String id, Collection<Annotation> annotations) {
			super(id, "annotationsfragment", SearchResultsPage.this);
			RepeatingView rv = new RepeatingView("annotation");
			for (Annotation ann : annotations)
				rv.add(new Label(rv.newChildId(), ann.getComment().getText()));
			add(rv);
		}
	}

	public class FileFragment extends Fragment {
		public FileFragment(String id, Databank db, Entry entry) {
			super(id, "filefragment", SearchResultsPage.this);
			ExternalLink el = new ExternalLink("link", db.getFilelink().replace("${PDBID}", entry.getPdbid()));
			el.add(new Label("path", entry.getFile().getPath()));
			add(el);
		}
	}
}
