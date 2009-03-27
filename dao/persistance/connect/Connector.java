package persistance.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class Connector {
	private static Connection	dbconnection	= null;
	private static Properties	dbconfig		= null;

	public static void connect(Properties dbconf) throws DBConnectionException {
		if (dbconf == null)
			throw new DBConnectionException("Invalid JDBC Connection settings supplied in Connector.connect();", null);
		dbconfig = dbconf;

		try {
			Class.forName(dbconf.getProperty("driver"));
			Connector.dbconnection = DriverManager.getConnection(dbconf.getProperty("protocol") + "//" + dbconf.getProperty("host") + ":" + dbconf.getProperty("port") + "/" + dbconf.getProperty("name"), dbconf.getProperty("user"), dbconf.getProperty("pass"));
		}
		catch (ClassNotFoundException e) {
			Connector.dbconnection = null;
			throw new DBConnectionException("Could not load jdbcdriver!", e);
		}
		catch (SQLException e) {
			Connector.dbconnection = null;
			throw new DBConnectionException("Could not connect to database!", e);
		}
	}

	public static Connection getConnection() throws DBConnectionException, SQLException {
		if (Connector.dbconnection == null || !Connector.dbconnection.isValid(0))
			connect(dbconfig);
		if (Connector.dbconnection == null || !Connector.dbconnection.isValid(0))
			throw new DBConnectionException("RDB not connected!", null);
		return Connector.dbconnection;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (Connector.dbconnection != null)
				Connector.dbconnection.close();
		}
		finally {
			super.finalize();
		}
	}
}
