package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Property;
import persistance.connect.Connector;

public abstract class Properties {

	private static final String name = "properties";

	private static final String[] pkey = { "name" };

	private static final String values = "name,explanation";

	private static final String[] valuesarray = Properties.values.split(",");

	public static Property getSingle(String propertyname) throws SQLException {
		Property returnProperty = null;
		String sql = "SELECT * FROM " + Properties.name;
		sql += " WHERE " + Properties.pkey[0] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, propertyname);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rsname = rs.getString(Properties.valuesarray[0]);
			String rsexplanation = rs.getString(Properties.valuesarray[1]);
			returnProperty = new Property(rsname, rsexplanation);
		}
		rs.close();
		ps.close();
		return returnProperty;
	}

	protected static boolean persist(Property property) {
		try {
			// See if Property already exists in the table
			Property rsProperty = Properties.getSingle(property.getName());
			if (rsProperty == null)
				return Properties.insert(property);
			else if (rsProperty.equals(property))
				return true;
			else
				return Properties.update(property);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(Property property) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"INSERT INTO " + Properties.name + " (" + Properties.values
						+ ") VALUES (?, ?)");
		ps.setString(1, property.getName());
		ps.setString(2, property.getExplanation());
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(Property property) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"UPDATE " + Properties.name + " SET "
						+ Properties.valuesarray[1] + " = ? WHERE "
						+ Properties.pkey[0] + " = ?");
		ps.setString(1, property.getExplanation());
		ps.setString(2, property.getName());
		ps.execute();
		ps.close();
		return true;
	}

	// protected static boolean remove(Property property) {
	// try {
	// PreparedStatement ps =
	// DBConnector.getConnection().prepareStatement("DELETE FROM " +
	// PropertyTable.name + " WHERE " + PropertyTable.pkey[0] + " = ?");
	// ps.setString(1, property.getName());
	// ps.execute();
	// ps.close();
	// return true;
	// }
	// catch (SQLException e) {
	// return false;
	// }
	// }
}
