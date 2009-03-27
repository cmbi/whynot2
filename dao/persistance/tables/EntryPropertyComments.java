package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import model.Comment;
import model.Database;
import model.PDBID;
import model.Property;
import persistance.connect.Connector;

public abstract class EntryPropertyComments {
	private static final String name = "entrypropertycomments";

	private static final String[] pkey = { "database", "pdbid", "property",
			"comment" };

	private static final String values = "database,pdbid,property,comment";

	private static final String[] valuesarray = EntryPropertyComments.values
			.split(",");

	public static Comment[] getCommentsForPropertyOfEntry(String database,
			String pdbid, String property) throws SQLException {
		Set<Comment> comments = new HashSet<Comment>();
		String sql = "SELECT * FROM " + EntryPropertyComments.name;
		sql += " WHERE " + EntryPropertyComments.pkey[0] + " LIKE ?";
		sql += " AND " + EntryPropertyComments.pkey[1] + " LIKE ?";
		sql += " AND " + EntryPropertyComments.pkey[2] + " LIKE ?";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database);
		ps.setString(2, pdbid);
		ps.setString(3, property);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int rscommentid = rs.getInt(EntryPropertyComments.valuesarray[3]);
			comments.add(Comments.getSingle(rscommentid));
		}
		rs.close();
		ps.close();
		return comments.toArray(new Comment[0]);
	}

	public static String[][] getCommentRecords() throws SQLException {
		List<String[]> commentrecords = new Vector<String[]>();
		String sql = "SELECT * FROM " + EntryPropertyComments.name;
		sql += " ORDER BY " + EntryPropertyComments.pkey[0] + ", " + EntryPropertyComments.pkey[1] + ", " + EntryPropertyComments.pkey[2];
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String database = rs.getString(EntryPropertyComments.valuesarray[0]);
			String pdbid = rs.getString(EntryPropertyComments.valuesarray[1]);
			String property = rs.getString(EntryPropertyComments.valuesarray[2]);
			int rscommentid = rs.getInt(EntryPropertyComments.valuesarray[3]);
			String comment = Comments.getSingle(rscommentid).getComment();

			String[] record = {database,pdbid,property,comment};
			commentrecords.add(record);
		}
		rs.close();
		ps.close();
		return commentrecords.toArray(new String[0][0]);
	}

	private static boolean contains(String pdbid, String database,
			String property, int comment) throws SQLException {
		boolean returnValue = false;
		String sql = "SELECT * FROM " + EntryPropertyComments.name;
		sql += " WHERE " + EntryPropertyComments.pkey[0] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[1] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[2] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[3] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database);
		ps.setString(2, pdbid);
		ps.setString(3, property);
		ps.setInt(4, comment);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rsfiletype = rs
					.getString(EntryPropertyComments.valuesarray[0]);
			String rsmsdid = rs.getString(EntryPropertyComments.valuesarray[1]);
			String rsproperty = rs
					.getString(EntryPropertyComments.valuesarray[2]);
			int rscommentid = rs.getInt(EntryPropertyComments.valuesarray[3]);
			if (rsfiletype.equals(database) && rsmsdid.equals(pdbid)
					&& rsproperty.equals(property) && rscommentid == comment)
				returnValue = true;
		}
		rs.close();
		ps.close();
		return returnValue;
	}

	public static boolean persist(PDBID pdbid, Database db, Property prop,
			Comment com) {
		try {
			// First make sure entryproperty exists, as we cant create it without boolean
			if (!EntryProperties.contains(pdbid.getPDBID(), db.getName(), prop.getName()))
				return false;

			// Then make sure the foreign key Comment exists & set correct comid
			if (!Comments.persist(com))
				return false;

			// See if the FileTypeProperty already exists
			if (EntryPropertyComments.contains(pdbid.getPDBID(), db.getName(),
					prop.getName(), com.getComid()))
				return true;
			else
				return EntryPropertyComments.insert(pdbid, db, prop, com);
		} catch (SQLException e) {
			return false;
		}
	}

	public static boolean persist(PDBID pdbid, Database db, Property prop,
			boolean bool, Comment com) {
		try {
			// First correctly set boolean value for entryproperty
			EntryProperties.persist(pdbid, db, prop, bool);

			// Then make sure the foreign key Comment exists & set correct comid
			if (!Comments.persist(com))
				return false;

			// See if the FileTypeProperty already exists
			if (EntryPropertyComments.contains(pdbid.getPDBID(), db.getName(),
					prop.getName(), com.getComid()))
				return true;
			else
				return EntryPropertyComments.insert(pdbid, db, prop, com);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(PDBID pdbid, Database database,
			Property property, Comment comment) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"INSERT INTO " + EntryPropertyComments.name + " ("
						+ EntryPropertyComments.values
						+ ") VALUES (?, ?, ?, ?)");
		ps.setString(1, database.getName());
		ps.setString(2, pdbid.getPDBID());
		ps.setString(3, property.getName());
		ps.setInt(4, comment.getComid());
		ps.execute();
		ps.close();
		return true;
	}

	public static boolean remove(PDBID pdbid, Database db, Property prop, Comment com) {
		try {
			if (PDBIDS.getSingle(pdbid.getPDBID()) == null)
				return true;
			if (Databases.getSingle(db.getName()) == null)
				return true;
			if (Properties.getSingle(prop.getName()) == null)
				return true;
			if (Comments.getSingle(com.getComment()) == null)
				return true;
			// If the entry property does not exist, the entrypropertycomment does not exist
			if (!EntryProperties.contains(pdbid.getPDBID(), db.getName(), prop.getName()))
				return true;

			if (!EntryPropertyComments.contains(pdbid.getPDBID(), db.getName(), prop.getName(), com.getComid()))
				return true;
			else
				return EntryPropertyComments.delete(pdbid, db, prop, com);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean delete(PDBID pdbid, Database database,
			Property property, Comment comment) throws SQLException {
		String sql = "DELETE FROM " + EntryPropertyComments.name;
		sql += " WHERE " + EntryPropertyComments.pkey[0] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[1] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[2] + " = ?";
		sql += " AND " + EntryPropertyComments.pkey[3] + " = ?";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database.getName());
		ps.setString(2, pdbid.getPDBID());
		ps.setString(3, property.getName());
		ps.setInt(4, comment.getComid());
		ps.execute();
		ps.close();
		return true;
	}
}
