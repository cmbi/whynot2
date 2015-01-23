package nl.ru.cmbi.whynot;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebContextInitializer implements ServletContextInitializer {
	@Override
	public void onStartup(final ServletContext sc) throws ServletException {
		// Ensure a session is open during all requests to prevent no open session in view
		FilterRegistration openSessionFilter = sc.addFilter("openentitymanagerinview",
				org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter.class);
		openSessionFilter.addMappingForUrlPatterns(null, false, "/*");

		// Setup Wicket as per: https://github.com/Pentadrago/spring-boot-example-wicket
		FilterRegistration filter = sc.addFilter("wicket-filter",
				org.apache.wicket.protocol.http.WicketFilter.class);
		filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
		filter.setInitParameter("applicationBean", "wicketApplication");
		// This line is the only surprise when comparing to the equivalent
		// web.xml. Without some initialization seems to be missing.
		filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
		filter.addMappingForUrlPatterns(null, false, "/*");
	}
}
