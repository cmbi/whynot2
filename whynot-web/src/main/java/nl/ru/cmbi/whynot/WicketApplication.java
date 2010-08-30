package nl.ru.cmbi.whynot;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import nl.ru.cmbi.whynot.about.AboutPage;
import nl.ru.cmbi.whynot.comment.CommentPage;
import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.databank.ListInitializer;
import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.error.MyExceptionErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.search.ResultsPage;
import nl.ru.cmbi.whynot.statistics.StatisticsPage;

public class WicketApplication extends WebApplication {
	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected void init() {
		//Set headless property for JFreeChart image generation
		System.setProperty("java.awt.headless", "true");

		//Spring
		addComponentInstantiationListener(new SpringComponentInjector(this));

		//RequestLogger
		//getRequestLoggerSettings().setRequestLoggerEnabled(true);

		//ErrorPages
		//getApplicationSettings().setAccessDeniedPage(MyExceptionErrorPage.class);
		//getApplicationSettings().setPageExpiredErrorPage(MyExceptionErrorPage.class);
		getApplicationSettings().setInternalErrorPage(MyExceptionErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

		//Pretty URLs
		mountBookmarkablePage("about", AboutPage.class);
		mountBookmarkablePage("comments", CommentPage.class);
		mountBookmarkablePage("databanks", DatabankPage.class);
		mountBookmarkablePage("entries", EntriesPage.class);
		mountBookmarkablePage("error", MyExceptionErrorPage.class);
		mountBookmarkablePage("search", ResultsPage.class);
		mountBookmarkablePage("statistics", StatisticsPage.class);

		//Register export lists as shared resources
		getSharedResources().putClassAlias(ListInitializer.class, "list");
		new ListInitializer().init(this);
	}

	@Override
	public RequestCycle newRequestCycle(Request request, Response response) {
		return new MyRequestCycle(this, (WebRequest) request, (WebResponse) response);
	}
}
