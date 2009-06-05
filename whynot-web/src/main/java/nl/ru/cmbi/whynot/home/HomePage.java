package nl.ru.cmbi.whynot.home;

import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

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
		ChildFragment childfragment = new ChildFragment("hierarchy", databankdao.findByName("PDB"));
		add(childfragment);
	}

	public class ChildFragment extends Fragment {
		private static final long	serialVersionUID	= -1982524056748695793L;

		public ChildFragment(String id, final Databank db) {
			super(id, "child", HomePage.this, new Model<Databank>(db));
			//Link
			Label lbl = new Label("name", db.getName());
			PageParameters pp = new PageParameters("name=" + db.getName());
			BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabankPage.class, pp);
			add(bpl.add(lbl));

			//Children
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new ChildFragment(children.newChildId(), child));
		}
	}
}
