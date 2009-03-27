package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Author;
import persistance.connect.Connector;

public abstract class Authors {

	private static final String name = "authors";

	private static final String[] pkey = { "name" };

	private static final String values = "name,email";

	private static final String[] valuesarray = Authors.values.split(",");

	protected static Author getSingle(String authorname) throws SQLException {
		Author returnAuthor = null;
		String sql = "SELECT * FROM " + Authors.name;
		sql += " WHERE " + Authors.pkey[0] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, authorname);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String name = rs.getString(Authors.valuesarray[0]);
			String email = rs.getString(Authors.valuesarray[1]);
			returnAuthor = new Author(name, email);
		}
		rs.close();
		ps.close();
		return returnAuthor;
	}

	protected static boolean persist(Author Author) {
		try {
			// See if Author already exists in the table
			Author rsAuthor = Authors.getSingle(Author.getName());
			if (rsAuthor == null)
				return Authors.insert(Author);
			else if (rsAuthor.equals(Author))
				return true;
			else
				return Authors.update(Author);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(Author Author) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"INSERT INTO " + Authors.name + " (" + Authors.values
						+ ") VALUES (?, ?)");
		ps.setString(1, Author.getName());
		ps.setString(2, Author.getEmail());
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(Author Author) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"UPDATE " + Authors.name + " SET " + Authors.valuesarray[1]
						+ " = ? WHERE " + Authors.pkey[0] + " = ?");
		ps.setString(1, Author.getEmail());
		ps.setString(2, Author.getName());
		ps.execute();
		ps.close();
		return true;
	}

	// protected static boolean delete(Author Author) {
	// try {
	// PreparedStatement ps =
	// DBConnector.getConnection().prepareStatement("DELETE FROM " +
	// AuthorTable.name + " WHERE " + AuthorTable.pkey[0] + " = ?");
	// ps.setString(1, Author.getName());
	// ps.execute();
	// ps.close();
	// return true;
	// }
	// catch (SQLException e) {
	// return false;
	// }
	// }
}
