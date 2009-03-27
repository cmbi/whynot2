package inout.database;

import inout.CommandlineWriter;
import model.Entry;
import persistance.IPersistance;
import persistance.RDB;

public class RDBWriter {
	private IPersistance db = new RDB(new JDBCXMLReader().getProperties());

	public RDBWriter() {
	}

	public boolean write(Entry entry) {
		if (this.db.addEntry(entry))
			return true;
		else
			CommandlineWriter.cmd.printError("Failed to store "
					+ entry.getDatabase().getName() + " File "
					+ entry.getPDBID() + " in RDB", "");
		return false;
	}
}
