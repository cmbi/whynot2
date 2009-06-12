package nl.ru.cmbi.whynot.home;

import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.feedback.FeedbackPanelWrapper;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.search.SearchPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HomePage extends WebPage {
	@SpringBean
	protected DatabankDAO	databankdao;

	public HomePage() {
		add(new FeedbackPanelWrapper("feedback"));
		add(new SearchPanel("search"));
		add(new DatabankHierarchyFragment("hierarchy", databankdao.findByName("PDB")));
	}

	public class DatabankHierarchyFragment extends Fragment {
		public DatabankHierarchyFragment(String id, final Databank db) {
			super(id, "hierarchyfragment", HomePage.this, new Model<Databank>(db));
			//Link
			PageParameters pp = new PageParameters();
			pp.put("name", db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(new Label("name", db.getName())));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new DatabankHierarchyFragment(children.newChildId(), child));
		}
	}
}