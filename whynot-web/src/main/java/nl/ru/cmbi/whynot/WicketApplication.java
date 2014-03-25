package nl.ru.cmbi.whynot;

import lombok.Setter;
import nl.ru.cmbi.whynot.error.MyExceptionErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;

import org.apache.wicket.Page;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
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
		
		getRequestCycleListeners().add(new AbstractRequestCycleListener() {
			
			@Override
			public IRequestHandler onException(RequestCycle cycle, Exception e)
			{
				IPageRequestHandler handler = PageRequestHandlerTracker.getLastHandler(cycle);
				
				IRequestablePage lastPage = handler!=null? handler.getPage() : null;
				
				return new RenderPageRequestHandler(new PageProvider(new MyExceptionErrorPage(lastPage, e)));
			}
		});

		// Spring
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));

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
	}
}
