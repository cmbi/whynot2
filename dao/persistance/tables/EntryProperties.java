package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Database;
import model.PDBID;
import model.Property;
import persistance.connect.Connector;

public abstract class EntryProperties {

	private static final String name = "entryproperties";

	private static final String[] pkey = { "database", "pdbid", "property" };

	private static final String values = "database,pdbid,property,boolean";

	private static final String[] valuesarray = EntryProperties.values
			.split(",");

	protected static boolean contains(String pdbid, String database,
			String property) throws SQLException {
		boolean found = false;
		String sql = "SELECT * FROM " + EntryProperties.name;
		sql += " WHERE " + EntryProperties.pkey[0] + " = ?";
		sql += " AND " + EntryProperties.pkey[1] + " = ?";
		sql += " AND " + EntryProperties.pkey[2] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database);
		ps.setString(2, pdbid);
		ps.setString(3, property);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			found = true;
		rs.close();
		ps.close();
		return found;
	}

	protected static boolean persist(PDBID pdbid, Database db, Property prop,
			boolean bool) {
		// First make sure the foreign key PDBID exists
		if (!PDBIDS.persist(pdbid))
			return false;

		// Then make sure the foreign key database exists
		if (!Databases.persist(db))
			return false;

		// Then make sure the foreign key property exists
		if (!Properties.persist(prop))
			return false;

		try {
			if (EntryProperties.contains(pdbid.getPDBID(), db.getName(), prop
					.getName()))
				return EntryProperties.update(pdbid, db, prop, bool);
			else
				return EntryProperties.insert(pdbid, db, prop, bool);
		} catch (SQLException e1) {
			return false;
		}
	}

	private static boolean insert(PDBID pdbid, Database db, Property prop,
			boolean bool) throws SQLException {
		String sql = "INSERT INTO " + EntryProperties.name;
		sql += " (" + EntryProperties.values + ")";
		sql += " VALUES (?, ?, ?, ?)";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, db.getName());
		ps.setString(2, pdbid.getPDBID());
		ps.setString(3, prop.getName());
		ps.setBoolean(4, bool);
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(PDBID pdbid, Database db, Property prop,
			boolean bool) throws SQLException {
		String sql = "UPDATE " + EntryProperties.name;
		sql += " SET " + EntryProperties.valuesarray[3] + " = ?";
		sql += " WHERE " + EntryProperties.pkey[0] + " = ?";
		sql += " AND " + EntryProperties.pkey[1] + " = ?";
		sql += " AND " + EntryProperties.pkey[2] + " = ?";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setBoolean(1, bool);
		ps.setString(2, db.getName());
		ps.setString(3, pdbid.getPDBID());
		ps.setString(4, prop.getName());
		ps.execute();
		ps.close();
		return true;
	}

	// protected static boolean delete(FileProperty fileproperty) {
	// try {
	// String sql = "DELETE FROM " + FilePropertyTable.name;
	// sql += " WHERE " + FilePropertyTable.pkey[0] + " = ?";
	// sql += " AND " + FilePropertyTable.pkey[1] + " = ?";
	// sql += " AND " + FilePropertyTable.pkey[2] + " = ?";
	// PreparedStatement ps = DBConnector.getConnection().prepareStatement(sql);
	// ps.setString(1, fileproperty.getFiletype().getName());
	// ps.setString(2, fileproperty.getMSD().getMsdid());
	// ps.setString(3, fileproperty.getProperty().getName());
	// ps.execute();
	// ps.close();
	// return true;
	// }
	// catch (SQLException e) {
	// return false;
	// }
	// }
}
