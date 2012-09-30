package nl.ru.cmbi.whynot;

import lombok.Setter;
import nl.ru.cmbi.whynot.databank.ListInitializer;
import nl.ru.cmbi.whynot.error.MyExceptionErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

@Component
public class WicketApplication extends WebApplication implements ApplicationContextAware {
	@Setter
	private ApplicationContext	applicationContext;

	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected void init() {
		// Set headless property for JFreeChart image generation
		System.setProperty("java.awt.headless", "true");

		// Spring
		addComponentInstantiationListener(new SpringComponentInjector(this));

		// RequestLogger
		// getRequestLoggerSettings().setRequestLoggerEnabled(true);

		// ErrorPages
		// getApplicationSettings().setAccessDeniedPage(MyExceptionErrorPage.class);
		// getApplicationSettings().setPageExpiredErrorPage(MyExceptionErrorPage.class);
		getApplicationSettings().setInternalErrorPage(MyExceptionErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

		// Annotation driven page mounting
		String packge = this.getClass().getPackage().getName();
		new AnnotatedMountScanner().scanPackage(packge).mount(this);

		// Register export lists as shared resources
		getSharedResources().putClassAlias(ListInitializer.class, "list");
		new ListInitializer().init(this);
	}

	@Override
	public RequestCycle newRequestCycle(final Request request, final Response response) {
		return new MyRequestCycle(this, (WebRequest) request, (WebResponse) response);
	}
}
