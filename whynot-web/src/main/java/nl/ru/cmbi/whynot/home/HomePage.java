package nl.ru.cmbi.whynot.home;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.feedback.FeedbackPanelWrapper;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.search.SearchPanel;

public class HomePage extends WebPage {
	@SpringBean
	protected DatabankDAO	databankdao;

	public HomePage() {
		add(new FeedbackPanelWrapper("feedback"));
		add(new SearchPanel("search"));
		add(new DatabankHierarchyFragment("hierarchy", databankdao.findByName("PDB"), databankdao.getAll()));
	}

	public class DatabankHierarchyFragment extends Fragment {
		public DatabankHierarchyFragment(String id, Databank db, List<Databank> list) {
			super(id, "hierarchyfragment", HomePage.this, new Model<Databank>(db));
			//Link
			PageParameters pp = new PageParameters();
			pp.put("name", db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(new Label("name", db.getName())));

			//Children
			RepeatingView children = new RepeatingView("children");
			for (Databank child : list)
				if (child.getParent().equals(db) && !child.equals(db))
					children.add(new DatabankHierarchyFragment(children.newChildId(), child, list));
			add(children);
		}
	}
}
