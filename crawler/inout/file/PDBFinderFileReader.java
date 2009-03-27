package inout.file;

import inout.CommandlineWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import model.Database;
import model.Entry;

public class PDBFinderFileReader implements Iterator<Entry> {
	private File file;

	private Database database;

	private Iterator<String> lineIterator;

	public PDBFinderFileReader(String path, Database database) {
		this.file = new File(path);
		this.database = database;

		List<String> validLines = new Vector<String>();
		try {
			String line, nextline;
			BufferedReader bf = new BufferedReader(new FileReader(this.file));
			while ((line = bf.readLine()) != null)
				if (line.matches("ID           : [\\d\\w]{4}"))
					if ((nextline = bf.readLine()) != null
							&& nextline.matches(".*:.*"))
						validLines.add(line);
			bf.close();
		} catch (FileNotFoundException e) {
			CommandlineWriter.cmd.printError(this.file.getName()
					+ " could not be read", "Reading file "
					+ this.file.getAbsolutePath() + " failed");
		} catch (IOException e) {
			CommandlineWriter.cmd.printError(this.file.getName()
					+ " could not be read", "Reading file "
					+ this.file.getAbsolutePath() + " failed");
		}
		this.lineIterator = validLines.iterator();
	}

	public boolean hasNext() {
		return this.lineIterator.hasNext();
	}

	public Entry next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		String line = this.lineIterator.next();
		return new Entry(line.substring(15, 19), this.file.getAbsolutePath(),
				this.file.lastModified(), this.database);
	}

	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
