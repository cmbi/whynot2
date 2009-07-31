package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.about.AboutPage;
import nl.ru.cmbi.whynot.comment.CommentPage;
import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.error.ErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.search.ResultsPage;
import nl.ru.cmbi.whynot.statistics.StatisticsPage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IExceptionSettings;

public class WicketApplication extends WebApplication {
	@Override
	protected void init() {
		//Spring
		//addComponentInstantiationListener(new SpringComponentInjector(this));

		//RequestLogger
		//getRequestLoggerSettings().setRequestLoggerEnabled(true);

		//ErrorPages
		getApplicationSettings().setAccessDeniedPage(ErrorPage.class);
		getApplicationSettings().setPageExpiredErrorPage(ErrorPage.class);
		getApplicationSettings().setInternalErrorPage(ErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

		//Pretty URLs
		mountBookmarkablePage("about", AboutPage.class);
		mountBookmarkablePage("comments", CommentPage.class);
		mountBookmarkablePage("databanks", DatabankPage.class);
		mountBookmarkablePage("entries", EntriesPage.class);
		mountBookmarkablePage("statistics", StatisticsPage.class);
		mountBookmarkablePage("search", ResultsPage.class);
	}

	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}
}
