package nl.ru.cmbi.whynot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import lombok.Setter;
import nl.ru.cmbi.whynot.databank.DatabankPage;
import nl.ru.cmbi.whynot.databank.ListInitializer;
import nl.ru.cmbi.whynot.error.MyExceptionErrorPage;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.search.ResultsPage;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.QueryParameter;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.request.mapper.AbstractBookmarkableMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import org.apache.wicket.SystemMapper;

import org.apache.wicket.request.mapper.mount.IMountedRequestMapper;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;

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
		
        // Register export lists as shared resources
        new ListInitializer().init(this);
        
        mount(new MountedMapper("/search", ResultsPage.class, new HybridPageParametersEncoder()));
        mount(new MountedMapper("/databanks", DatabankPage.class, new HybridPageParametersEncoder()));
	}
	
	private class HybridPageParametersEncoder implements IPageParametersEncoder {
		
		@Override
		public PageParameters decodePageParameters(Url url)
	    {
	        PageParameters parameters = new PageParameters();

	        int i = 0;
	        for (Iterator<String> segment = url.getSegments().iterator(); segment.hasNext(); ) {
	            String key = segment.next();
	            String value = segment.next();

	            parameters.add(key, value);
	        }
	        for (QueryParameter p : url.getQueryParameters() ) {

	            parameters.add(p.getName(), p.getValue());
	        }

	        return parameters.isEmpty() ? null : parameters;
	    }

		@Override
	    public Url encodePageParameters(PageParameters pageParameters)
	    {
	        Url url = new Url();

	        for (PageParameters.NamedPair pair : pageParameters.getAllNamed()) {
	            url.getSegments().add(pair.getKey());
	            url.getSegments().add(pair.getValue());
	        }

	        return url;
	    }
	}
}
