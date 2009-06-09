package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.about.AboutPage;
import nl.ru.cmbi.whynot.comment.CommentPage;
import nl.ru.cmbi.whynot.databank.DatabankEntriesPage;
import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.error.ErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.search.SearchResultsPage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

public class WicketApplication extends WebApplication {
	@Override
	protected void init() {
		//Spring
		addComponentInstantiationListener(new SpringComponentInjector(this));

		//ErrorPages
		IApplicationSettings settings = getApplicationSettings();
		settings.setAccessDeniedPage(ErrorPage.class);
		settings.setPageExpiredErrorPage(ErrorPage.class);
		settings.setInternalErrorPage(ErrorPage.class);

		//Pretty URLs
		mountBookmarkablePage("about", AboutPage.class);
		mountBookmarkablePage("comments", CommentPage.class);
		mountBookmarkablePage("databanks", DatabankPage.class);
		mountBookmarkablePage("entries", DatabankEntriesPage.class);
		mountBookmarkablePage("search", SearchResultsPage.class);
	}

	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}
}
