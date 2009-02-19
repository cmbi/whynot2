package inout.database;

import inout.Progress;
import inout.ProgressWriter;

import java.io.File;
import java.util.Iterator;

import model.Database;
import model.Entry;
import persistance.IPersistance;
import persistance.RDB;

/**
 * This class validates every record in the file table by reading the filepath
 * and checking to see if the file on filepath exists on the local machine. If
 * the file exists and has the same timestamp, the record is validated and the
 * record remains untouched. If the file does not exist or has a different
 * timestamp, the record is removed from the table.
 */
public class RDBValidator {
	public RDBValidator(Database filetype) {
		IPersistance db = new RDB(new JDBCXMLReader().getProperties());

		Progress progress = new Progress(
				"Verifying entries in database exist locally");
		ProgressWriter pgwrtr = new ProgressWriter(progress);

		Iterator<Entry> entryIterator = new RDBReader(filetype);
		while (entryIterator.hasNext()) {
			Entry entry = entryIterator.next();
			File localfile = new File(entry.getFilepath());
			if (localfile.exists()
					&& localfile.lastModified() == entry.getTimestamp())
				progress.increaseNoSucces();
			else if (db.removeEntry(entry))
				progress.increaseNoSucces();
			else
				progress.increaseNoFailed();
		}
		pgwrtr.stop();
	}
}
