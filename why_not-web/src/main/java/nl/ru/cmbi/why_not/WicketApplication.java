package nl.ru.cmbi.why_not;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see nl.ru.cmbi.why_not.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	@Override
	protected void init() {
		addComponentInstantiationListener(new SpringComponentInjector(this));
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage() {
		//return PieChartExamplePage.class;
		return HomePage.class;
	}

}
