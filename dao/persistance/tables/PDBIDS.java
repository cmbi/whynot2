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

public abstract class PDBIDS {

	private static final String name = "pdbids";

	private static final String pkey = "pdbid";

	private static final String[] valuesarray = { "pdbid" };

	protected static PDBID[] getMultiple(String pattern) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>();
		String sql = "SELECT * FROM " + PDBIDS.name;
		sql += " WHERE " + PDBIDS.pkey + " LIKE ?";
		sql += " ORDER BY " + PDBIDS.pkey;
		PreparedStatement stmt = Connector.getConnection()
				.prepareStatement(sql);
		stmt.setString(1, pattern);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			String rsmsdid = rs.getString(PDBIDS.valuesarray[0]);
			pdbids.add(new PDBID(rsmsdid));
		}
		rs.close();
		stmt.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID getSingle(String pdbid) throws SQLException {
		PDBID returnPDBID = null;
		String sql = "SELECT * FROM " + PDBIDS.name;
		sql += " WHERE " + PDBIDS.pkey + " = ?";
		sql += " LIMIT 1";
		PreparedStatement stmt = Connector.getConnection()
				.prepareStatement(sql);
		stmt.setString(1, pdbid);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			String rsmsdid = rs.getString(PDBIDS.valuesarray[0]);
			returnPDBID = new PDBID(rsmsdid);
		}
		rs.close();
		stmt.close();
		return returnPDBID;
	}

	private static boolean contains(String pdbid) throws SQLException {
		return PDBIDS.getSingle(pdbid) != null;
	}

	protected static boolean persist(PDBID pdbid) {
		try {
			if (PDBIDS.contains(pdbid.getPDBID()))
				return true;
			else if (PDBIDS.insert(pdbid)) {
				// Set msd / all filetypes combinations to not exist
				for (Database database : Databases.getMultiple("%")) {
					Property existsprop = Properties.getSingle("Exists");
					if (existsprop == null)
						existsprop = new Property("Exists", "");
					EntryProperties.persist(pdbid, database, existsprop, false);
				}
				return true;
			} else
				return false;
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(PDBID pdbid) throws SQLException {
		String sql = "INSERT INTO " + PDBIDS.name + " ("
				+ PDBIDS.valuesarray[0] + ") VALUES (?)";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, pdbid.getPDBID());
		ps.execute();
		ps.close();
		return true;
	}
}
