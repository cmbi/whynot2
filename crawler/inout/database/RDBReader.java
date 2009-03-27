package inout.database;

import inout.CommandlineWriter;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import model.Database;
import model.Entry;
import persistance.IPersistance;
import persistance.PersistanceException;
import persistance.RDB;

public class RDBReader implements Iterator<Entry> {
	private Database database;

	private Iterator<Entry> entryIterator;

	public RDBReader(Database database) {
		this.database = database;
		this.entryIterator = this.getEntryIterator();
	}

	private Iterator<Entry> getEntryIterator() {
		IPersistance db = new RDB(new JDBCXMLReader().getProperties());
		Entry[] entries;
		try {
			entries = db.getEntriesForDatabase(this.database);
		} catch (PersistanceException e) {
			entries = new Entry[0];
			CommandlineWriter.cmd.printPersistanceError(
					"DBReader.getEntryIterator", e);
		}

		List<Entry> entryList = new Vector<Entry>();
		for (Entry entry : entries)
			entryList.add(entry);
		return entryList.iterator();
	}

	public boolean hasNext() {
		return this.entryIterator.hasNext();
	}

	public Entry next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		return this.entryIterator.next();
	}

	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
