package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.databank.DatabanksPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HomePage extends WebPage {
	@SpringBean
	protected DatabankDAO	databankdao;

	public HomePage() {
		ListView<Databank> list = new ListView<Databank>("databanklist", databankdao.findAll()) {
			private static final long	serialVersionUID	= 7999802380058749939L;

			@Override
			protected void populateItem(ListItem<Databank> item) {
				Databank db = item.getModelObject();
				Label lbl = new Label("name", db.getName());
				PageParameters pp = new PageParameters("name=" + db.getName());
				BookmarkablePageLink<WebPage> bpl = new BookmarkablePageLink<WebPage>("databank", DatabanksPage.class, pp);
				item.add(bpl.add(lbl));
			}
		};
		add(list);
	}

	public HomePage(PageParameters parameters) {
		this();
	}
}
