package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.comment.CommentsPage;
import nl.ru.cmbi.whynot.databank.DatabanksPage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

public class WicketApplication extends WebApplication {
	@Override
	protected void init() {
		addComponentInstantiationListener(new SpringComponentInjector(this));

		mountBookmarkablePage("databanks", DatabanksPage.class);
		mountBookmarkablePage("comments", CommentsPage.class);
	}

	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}
}
