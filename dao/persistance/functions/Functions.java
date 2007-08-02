package persistance.functions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import model.PDBID;
import persistance.connect.Connector;
import persistance.tables.Databases;

public abstract class Functions {
	public static void CalculateDatabaseStats(String childdatabase) throws SQLException {
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM SETDATABASESTATS(?,?)");
		ps.setString(1, childdatabase);
		ps.setString(2, Databases.getParent(childdatabase));
		ps.executeQuery();
		ps.close();
	}
	
	public static PDBID[] PDBIDsInDB(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsInDB(?)");
		ps.setString(1, childdatabase);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsInDBNotObsolete(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsInDBNotObsolete(?,?)");
		ps.setString(1, childdatabase);
		ps.setString(2, Databases.getParent(childdatabase));
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsInDBObsolete(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsNotInDB(?,?)");
		ps.setString(1, Databases.getParent(childdatabase));
		ps.setString(2, childdatabase);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsNotInDB(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsNotInDB(?,?)");
		ps.setString(1, childdatabase);
		ps.setString(2, Databases.getParent(childdatabase));
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsNotInDBWithComment(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsNotInDBWithComment(?,?)");
		ps.setString(1, childdatabase);
		ps.setString(2, Databases.getParent(childdatabase));
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsNotInDBWithoutComment(String childdatabase) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsNotInDBWithoutComment(?,?)");
		ps.setString(1, childdatabase);
		ps.setString(2, Databases.getParent(childdatabase));
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}

	public static PDBID[] PDBIDsWithComment(int comid) throws SQLException {
		List<PDBID> pdbids = new Vector<PDBID>(0);
		PreparedStatement ps = Connector.getConnection().prepareStatement(
				"SELECT * FROM PDBIDsWithComment(?)");
		ps.setInt(1, comid);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			pdbids.add(new PDBID(rs.getString("pdbid")));
		rs.close();
		ps.close();
		return pdbids.toArray(new PDBID[0]);
	}
}
