package persistance.functions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import persistance.connect.Connector;

public class RDBStats {
	private int pdbids;
	private int databases;
	private int entries;

	private int comments;
	private int properties;
	private int entrypropertycomments;

	public RDBStats() throws SQLException {
		PreparedStatement ps1 = Connector.getConnection().prepareStatement("SELECT count(*) FROM PDBIDS");
		ResultSet rs1 = ps1.executeQuery();
		rs1.next();
		this.pdbids = rs1.getInt(1);
		rs1.close();
		ps1.close();

		PreparedStatement ps2 = Connector.getConnection().prepareStatement("SELECT count(*) FROM DATABASES");
		ResultSet rs2 = ps2.executeQuery();
		rs2.next();
		this.databases = rs2.getInt(1);
		rs2.close();
		ps2.close();

		PreparedStatement ps3 = Connector.getConnection().prepareStatement("SELECT count(*) FROM ENTRIES");
		ResultSet rs3 = ps3.executeQuery();
		rs3.next();
		this.entries = rs3.getInt(1);
		rs3.close();
		ps3.close();

		PreparedStatement ps4 = Connector.getConnection().prepareStatement("SELECT count(*) FROM COMMENTS");
		ResultSet rs4 = ps4.executeQuery();
		rs4.next();
		this.comments = rs4.getInt(1);
		rs4.close();
		ps4.close();

		PreparedStatement ps5 = Connector.getConnection().prepareStatement("SELECT count(*) FROM PROPERTIES");
		ResultSet rs5 = ps5.executeQuery();
		rs5.next();
		this.properties = rs5.getInt(1);
		rs5.close();
		ps5.close();

		PreparedStatement ps6 = Connector.getConnection().prepareStatement("SELECT count(*) FROM ENTRYPROPERTYCOMMENTS");
		ResultSet rs6 = ps6.executeQuery();
		rs6.next();
		this.entrypropertycomments = rs6.getInt(1);
		rs6.close();
		ps6.close();
	}

	public int getPdbids() {
		return pdbids;
	}

	public int getDatabases() {
		return databases;
	}

	public int getEntries() {
		return entries;
	}

	public int getComments() {
		return comments;
	}

	public int getProperties() {
		return properties;
	}

	public int getEntrypropertycomments() {
		return entrypropertycomments;
	}
}
