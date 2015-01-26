package nl.ru.cmbi.whynot.home;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.feedback.FeedbackPanelWrapper;
import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.search.SearchPanel;
import nl.ru.cmbi.whynot.util.Utils;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends WebPage {
	
	Logger log = LoggerFactory.getLogger(HomePage.class);
	
	@SpringBean
	protected DatabankRepo	databankdao;

	public HomePage() {
		add(new FeedbackPanelWrapper("feedback"));
		add(new SearchPanel("search"));
		add(new DatabankHierarchyFragment("hierarchy", databankdao.getRoot(), databankdao.findAll()));
	}

	public class DatabankHierarchyFragment extends Fragment {
		public DatabankHierarchyFragment(final String id, final Databank db, final List<Databank> list) {
			super(id, "hierarchyfragment", HomePage.this, new Model<Databank>(db));
			
			String name=Utils.hierarchyName(db);

			// Link
			PageParameters pp = new PageParameters();
			pp.add("name", db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(new Label("name", name)));

			// Children
			RepeatingView children = new RepeatingView("children");
			for (Databank child : list)
				if (child.getParent().equals(db) && !child.equals(db))
					children.add(new DatabankHierarchyFragment(children.newChildId(), child, list));
			add(children);
		}
	}
}
