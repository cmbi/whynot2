package persistance.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import model.Entry;
import model.Property;
import persistance.connect.Connector;

public abstract class Entries {

	private static final String name = "entries";

	private static final String[] pkey = { "database", "pdbid" };

	private static final String values = "database,pdbid,filepath,timestamp";

	private static final String[] valuesarray = Entries.values.split(",");

	public static Entry[] getMultiple(String database, String pattern)
			throws SQLException {
		List<Entry> MSDFile = new Vector<Entry>();
		String sql = "SELECT * FROM " + Entries.name;
		sql += " WHERE " + Entries.pkey[0] + " LIKE ?";
		sql += " AND " + Entries.pkey[1] + " LIKE ?";
		sql += " ORDER BY " + Entries.pkey[0] + "," + Entries.pkey[1];
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database);
		ps.setString(2, pattern);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String msdid = rs.getString(Entries.valuesarray[1]);
			String filepath = rs.getString(Entries.valuesarray[2]);
			Timestamp timestamp = rs.getTimestamp(Entries.valuesarray[3]);
			String type = rs.getString(Entries.valuesarray[0]);
			MSDFile.add(new Entry(msdid, filepath, timestamp.getTime(),
					Databases.getSingle(type)));
		}
		rs.close();
		ps.close();
		return MSDFile.toArray(new Entry[0]);
	}

	public static Entry getSingle(String database, String pdbid)
			throws SQLException {
		Entry returnMSDFile = null;
		String sql = "SELECT * FROM " + Entries.name;
		sql += " WHERE " + Entries.pkey[0] + " = ?";
		sql += " AND " + Entries.pkey[1] + " = ?";
		sql += " LIMIT 1";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, database);
		ps.setString(2, pdbid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rstype = rs.getString(Entries.valuesarray[0]);
			String rsmsdid = rs.getString(Entries.valuesarray[1]);
			String rsfilepath = rs.getString(Entries.valuesarray[2]);
			Timestamp rstimestamp = rs.getTimestamp(Entries.valuesarray[3]);
			returnMSDFile = new Entry(rsmsdid, rsfilepath, rstimestamp
					.getTime(), Databases.getSingle(rstype));
		}
		rs.close();
		ps.close();
		return returnMSDFile;
	}

	public static boolean persist(Entry entry) {
		// First make sure the foreign key PDBID exists
		if (!PDBIDS.persist(entry))
			return false;

		// Then make sure the foreign key database exists
		if (!Databases.persist(entry.getDatabase()))
			return false;

		try {

			// See if entry already exists in the table
			Entry rsentry = Entries.getSingle(entry.getDatabase().getName(),
					entry.getPDBID());
			if (rsentry == null)
				if (Entries.insert(entry)) {
					Property existsprop = Properties.getSingle("Exists");
					if (existsprop == null)
						existsprop = new Property("Exists", "");
					// Set entry combination to exist
					EntryProperties.persist(entry, entry.getDatabase(),
							existsprop, true);
					return true;
				} else
					return false;
			else if (rsentry.equals(entry))
				return true;
			else
				return Entries.update(entry);
		} catch (SQLException e) {
			return false;
		}
	}

	private static boolean insert(Entry entry) throws SQLException {
		String sql = "INSERT INTO " + Entries.name + " (" + Entries.values
				+ ") VALUES (?, ?, ?, ?)";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, entry.getDatabase().getName());
		ps.setString(2, entry.getPDBID());
		ps.setString(3, entry.getFilepath());
		ps.setTimestamp(4, new Timestamp(entry.getTimestamp()));
		ps.execute();
		ps.close();
		return true;
	}

	private static boolean update(Entry msdfile) throws SQLException {
		String sql = "UPDATE " + Entries.name;
		sql += " SET " + Entries.valuesarray[2] + " = ?,";
		sql += " " + Entries.valuesarray[3] + " = ?";
		sql += " WHERE " + Entries.pkey[0] + " = ?";
		sql += " AND " + Entries.pkey[1] + " = ?";
		PreparedStatement ps = Connector.getConnection().prepareStatement(sql);
		ps.setString(1, msdfile.getFilepath());
		ps.setTimestamp(2, new Timestamp(msdfile.getTimestamp()));
		ps.setString(3, msdfile.getDatabase().getName());
		ps.setString(4, msdfile.getPDBID());
		ps.execute();
		ps.close();
		return true;
	}

	public static boolean remove(Entry entry) {
		try {
			String sql = "DELETE FROM " + Entries.name;
			sql += " WHERE " + Entries.pkey[0] + " = ?";
			sql += " AND " + Entries.pkey[1] + " = ?";
			PreparedStatement ps = Connector.getConnection().prepareStatement(
					sql);
			ps.setString(1, entry.getDatabase().getName());
			ps.setString(2, entry.getPDBID());
			ps.execute();
			ps.close();

			Property existsprop = Properties.getSingle("Exists");
			if (existsprop == null)
				existsprop = new Property("Exists", "");
			// Set entry to not exist
			EntryProperties.persist(entry, entry.getDatabase(), existsprop,
					false);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
