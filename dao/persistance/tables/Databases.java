package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import model.Database;
import model.PDBID;
import model.Property;
import persistance.connect.Connector;
import persistance.functions.DBStats;

public abstract class Databases {

	private static final String name = "databases";

	private static final String[] pkey = { "name" };

	// Special case because comid is serial and should not be inserted manually
	private static final String insertValues = "name,regex,pdbidoffset,reference,filelink";

	private static final String values = insertValues
			+ ",indball,indbcorrect,indbincorrect,notindball,notindbcorrect,notindbincorrect";

	private static final String[] valuesarray = Databases.values.split(",");

	public static String getParent(String name) {
		return (name.equals("HSSP")) ? "DSSP" : "PDB";
	}

	public static Database[] getMultiple(String pattern) throws SQLException {
		List<Database> filetypes = new Vector<Database>();
		String sql = "SELECT * FROM " + Databases.name;
		sql += " WHERE " + Databases.pkey[0] + " LIKE ?";
		sql += " ORDER BY " + Databases.pkey[0];
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, pattern);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rstypename = rs.getString(Databases.valuesarray[0]);
			String rsregex = rs.getString(Databases.valuesarray[1]);
			int rsoffset = rs.getInt(Databases.valuesarray[2]);
			Database newFileType = new Database(rstypename, rsregex, rsoffset);
			newFileType.setReference(rs.getString(Databases.valuesarray[3]));
			newFileType.setFilelink(rs.getString(Databases.valuesarray[4]));
			filetypes.add(newFileType);
		}
		rs.close();
		ps.close();
		return filetypes.toArray(new Database[0]);
	}

	public static Database getSingle(String name) throws SQLException {
		Database returnFileType = null;
		String sql = "SELECT * FROM " + Databases.name;
		sql += " WHERE " + Databases.pkey[0] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rstypename = rs.getString(Databases.valuesarray[0]);
			String rsregex = rs.getString(Databases.valuesarray[1]);
			int rsoffset = rs.getInt(Databases.valuesarray[2]);
			returnFileType = new Database(rstypename, rsregex, rsoffset);
			returnFileType.setReference(rs.getString(Databases.valuesarray[3]));
			returnFileType.setFilelink(rs.getString(Databases.valuesarray[4]));
		}
		rs.close();
		ps.close();
		return returnFileType;
	}

	protected static boolean persist(Database database) {
		try {
			Property existsprop = Properties.getSingle("Exists");
			if (existsprop == null)
				existsprop = new Property("Exists", "");

			// See if filetype already exists in the table
			Database rsdatabase = Databases.getSingle(database.getName());

			if (rsdatabase == null)
				if (Databases.insert(database)) {
					// Set all msds / filetype combinations to not exist
					for (PDBID pdbid : PDBIDS.getMultiple("%"))
						EntryProperties.persist(pdbid, database, existsprop,
								false);
					return true;
				} else
					return false;
			else if (rsdatabase.equals(database))
				return true;
			else
				return Databases.update(database);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(Database filetype) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"INSERT INTO " + Databases.name + " (" + Databases.insertValues
						+ ") VALUES (?, ?, ?, ?, ?)");
		ps.setString(1, filetype.getName());
		ps.setString(2, filetype.getRegex());
		ps.setInt(3, filetype.getOffset());
		ps.setString(4, filetype.getReference());
		ps.setString(5, filetype.getFilelink());
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(Database filetype) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"UPDATE " + Databases.name + " SET " + Databases.valuesarray[1]
						+ " = ?," + Databases.valuesarray[2] + " = ?,"
						+ Databases.valuesarray[3] + " = ?,"
						+ Databases.valuesarray[4] + " = ? WHERE "
						+ Databases.pkey[0] + " = ?");
		ps.setString(1, filetype.getRegex());
		ps.setInt(2, filetype.getOffset());
		ps.setString(3, filetype.getReference());
		ps.setString(4, filetype.getFilelink());
		ps.setString(5, filetype.getName());
		ps.execute();
		ps.close();
		return true;
	}

	public static DBStats getDBStats(String dbname) throws SQLException {
		int[] stats = new int[6];
		String sql = "SELECT * FROM " + Databases.name;
		sql += " WHERE " + Databases.pkey[0] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, dbname);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			stats[0] = rs.getInt(Databases.valuesarray[5]);
			stats[1] = rs.getInt(Databases.valuesarray[6]);
			stats[2] = rs.getInt(Databases.valuesarray[7]);
			stats[3] = rs.getInt(Databases.valuesarray[8]);
			stats[4] = rs.getInt(Databases.valuesarray[9]);
			stats[5] = rs.getInt(Databases.valuesarray[10]);
		}
		rs.close();
		ps.close();
		return new DBStats(stats);
	}

	// protected static boolean delete(FileType filetype) {
	// try {
	// PreparedStatement ps =
	// DBConnector.getConnection().prepareStatement("DELETE FROM " +
	// FileTypeTable.name + " WHERE " + FileTypeTable.pkey[0] + " = ?");
	// ps.setString(1, filetype.getName());
	// ps.execute();
	// ps.close();
	// return true;
	// }
	// catch (SQLException e) {
	// return false;
	// }
	// }
}
