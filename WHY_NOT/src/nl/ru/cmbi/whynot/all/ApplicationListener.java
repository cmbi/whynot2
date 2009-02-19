package nl.ru.cmbi.whynot.all;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import model.Database;
import persistance.IPersistance;
import persistance.RDB;
import persistance.connect.DBConnectionException;

public final class ApplicationListener implements ServletContextListener {
	// ------------------------------------------------------ Instance Variables

	/**
	 * <p>The <code>ServletContext</code> for this web application.</p>
	 */
	private ServletContext		context			= null;

	/**
	 * The {@link Database} object we construct and make available.
	 */
	private IPersistance			rdb		= null;

    /**
     * <p>Application scope attribute key under which the valid selection
     * items for the protocol property is stored.</p>
     */
    public static final String PROTOCOLS_KEY = "protocols";

	// ------------------------------------------ ServletContextListener Methods

	/**
	 * <p>Initialize and load our initial database from persistent
	 * storage.</p>
	 *
	 * @param event The context initialization event
	 */
	public void contextInitialized(ServletContextEvent event) {
		// Remember our associated ServletContext
		this.context = event.getServletContext();

		try {
			java.util.Properties dbconf =  new java.util.Properties();

			dbconf.setProperty("driver","org.postgresql.Driver");
			dbconf.setProperty("protocol","jdbc:postgresql:");
			dbconf.setProperty("host","127.0.0.1");
			dbconf.setProperty("port","5432");
			dbconf.setProperty("name","whynot");
			dbconf.setProperty("user","whynot_owner");
			dbconf.setProperty("pass","");

			try {
				Context env = (Context)new InitialContext().lookup("java:comp/env");
				dbconf.setProperty("host", (String)env.lookup("whynot-db-host"));
				dbconf.setProperty("port", (String)env.lookup("whynot-db-port"));
				dbconf.setProperty("user", (String)env.lookup("whynot-db-user"));
				dbconf.setProperty("pass", (String)env.lookup("whynot-db-pass"));
			} catch (NamingException e) {
				dbconf.setProperty("host", event.getServletContext().getInitParameter("whynot-db-host"));
				dbconf.setProperty("port", event.getServletContext().getInitParameter("whynot-db-port"));
				dbconf.setProperty("user", event.getServletContext().getInitParameter("whynot-db-user"));
				dbconf.setProperty("pass", event.getServletContext().getInitParameter("whynot-db-pass"));
			}

			// Construct a new database and make it available
			this.rdb = new RDB(dbconf);
		}
		catch (DBConnectionException e) {
			e.printStackTrace();
			throw new IllegalStateException("Application Listener: Cannot load Relational Database!");
		}

		this.context.setAttribute(Constants.RDB_KEY, this.rdb);
	}

	/**
	 * <p>Gracefully shut down this database, releasing any resources that
	 * were allocated at initialization.</p>
	 *
	 * @param event ServletContextEvent to process
	 */
	public void contextDestroyed(@SuppressWarnings("unused")
	ServletContextEvent event) {
		this.context.removeAttribute(Constants.RDB_KEY);
		this.context.removeAttribute(PROTOCOLS_KEY);
		this.rdb = null;
		this.context = null;
	}
}
