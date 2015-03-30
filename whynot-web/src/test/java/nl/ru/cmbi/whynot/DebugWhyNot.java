package nl.ru.cmbi.whynot;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class DebugWhyNot {
	public static void main(final String[] args) throws Exception {
		new SpringApplicationBuilder(
				EmbeddedServletContainerAutoConfiguration.class,
				WicketApplication.class).headless(false).run(args);
		//Desktop.getDesktop().browse(new URI("http://localhost:8080/"));
	}
}
