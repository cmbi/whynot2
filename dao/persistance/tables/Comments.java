package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import model.Author;
import model.Comment;
import persistance.connect.Connector;

public abstract class Comments {

	private static final String name = "comments";

	private static final String[] pkey = { "comid" };

	// Special case because comid is serial and should not be inserted manually
	private static final String insertValues = "author,comment,timestamp";

	// Special case because comid should be read when reading a record
	private static final String values = "comid," + Comments.insertValues;

	private static final String[] valuesarray = Comments.values.split(",");

	public static Comment[] getMultiple(String pattern) throws SQLException {
		List<Comment> comments = new Vector<Comment>();
		String sql = "SELECT * FROM " + Comments.name;
		sql += " WHERE " + Comments.valuesarray[2] + " LIKE ?";
		sql += " ORDER BY " + Comments.pkey[0];
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, pattern);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int comid = rs.getInt(Comments.valuesarray[0]);
			Author author = Authors.getSingle(rs
					.getString(Comments.valuesarray[1]));
			String rscontent = rs.getString(Comments.valuesarray[2]);
			Timestamp timestamp = rs.getTimestamp(Comments.valuesarray[3]);
			comments.add(new Comment(comid, author, rscontent, timestamp.getTime()));
		}
		rs.close();
		ps.close();
		return comments.toArray(new Comment[0]);
	}

	public static Comment getSingle(int comid) throws SQLException {
		Comment returnComment = null;
		String sql = "SELECT * FROM " + Comments.name;
		sql += " WHERE " + Comments.pkey[0] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setInt(1, comid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Author author = Authors.getSingle(rs
					.getString(Comments.valuesarray[1]));
			String rscontent = rs.getString(Comments.valuesarray[2]);
			Timestamp timestamp = rs.getTimestamp(Comments.valuesarray[3]);
			returnComment = new Comment(comid, author, rscontent, timestamp
					.getTime());
		}
		rs.close();
		ps.close();
		return returnComment;
	}

	public static Comment getSingle(String comment) throws SQLException {
		Comment returnComment = null;
		String sql = "SELECT * FROM " + Comments.name;
		sql += " WHERE " + Comments.valuesarray[2] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, comment);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int comid = rs.getInt(Comments.valuesarray[0]);
			Author author = Authors.getSingle(rs
					.getString(Comments.valuesarray[1]));
			String rscontent = rs.getString(Comments.valuesarray[2]);
			Timestamp timestamp = rs.getTimestamp(Comments.valuesarray[3]);
			returnComment = new Comment(comid, author, rscontent, timestamp
					.getTime());
		}
		rs.close();
		ps.close();
		return returnComment;
	}

	protected static boolean persist(Comment comment) {
		// First make sure the foreign key Author exists
		if (!Authors.persist(comment.getAuthor()))
			return false;

		try {
			// See if Comment already exists in the table
			Comment rsComment = Comments.getSingle(comment.getComment());
			if (rsComment == null) {
				if (Comments.insert(comment)) {
					rsComment = Comments.getSingle(comment.getComment());
					if (rsComment == null)
						return false;
					else { // Ensure Comment comment gets the correct comid
						// assigned
						comment.setComid(rsComment.getComid());
						return true;
					}
				} else
					return false;
			} else { // Ensure Comment comment gets the correct comid
				// assigned
				comment.setComid(rsComment.getComid());
				return Comments.update(comment);
			}
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(Comment comment) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"INSERT INTO " + Comments.name + " (" + Comments.insertValues
						+ ") VALUES (?, ?, ?)");
		ps.setString(1, comment.getAuthor().getName());
		ps.setString(2, comment.getComment());
		ps.setTimestamp(3, new Timestamp(comment.getTimestamp()));
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(Comment comment) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"UPDATE " + Comments.name + " SET " + Comments.valuesarray[1]
						+ " = ?," + Comments.valuesarray[2] + " = ?,"
						+ Comments.valuesarray[3] + " = ? WHERE "
						+ Comments.pkey[0] + " = ?");
		ps.setString(1, comment.getAuthor().getName());
		ps.setString(2, comment.getComment());
		ps.setTimestamp(3, new Timestamp(comment.getTimestamp()));
		ps.setInt(4, comment.getComid());
		ps.execute();
		ps.close();
		return true;
	}

	// protected static boolean delete(Comment comment) {
	// try {
	// PreparedStatement ps =
	// DBConnector.getConnection().prepareStatement("DELETE FROM " +
	// CommentTable.name + " WHERE " + CommentTable.pkey[0] + " = ?");
	// ps.setInt(1, comment.getComid());
	// ps.execute();
	// ps.close();
	// return true;
	// }
	// catch (SQLException e) {
	// return false;
	// }
	// }
}
