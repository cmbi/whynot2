package nl.ru.cmbi.whynot;

import java.awt.Desktop;
import java.net.URI;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class StartWhyNot {
	public static void main(final String[] args) throws Exception {
		// Mock database and contents
		// XXX DBMock.main(new String[0]);

		// Setup server to host web app
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8082);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");
		server.addHandler(bb);

		System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
		server.start();

		// Launch browser
		Desktop.getDesktop().browse(new URI("http://localhost:" + connector.getPort() + bb.getContextPath()));

		System.in.read();
		System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
		server.stop();
		server.join();
	}
}