package inout.file;

import inout.CommandlineWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.Entry;

public class CrawledFilesWriter {
	private String filename = "crawled.txt";

	public CrawledFilesWriter(String prefix) {
		File directory = new File("./crawled/");
		if (directory.exists() || directory.mkdir())
			prefix = "./crawled/" + prefix;
		this.filename = prefix + "_" + this.filename;
		try {
			File f = new File(this.filename);
			FileWriter fw = new FileWriter(f);
			new BufferedWriter(fw).close(); // Clear file contents
		} catch (IOException e) {
			CommandlineWriter.cmd.printFatalError("Writing " + this.filename
					+ " failed", "Crawled files not stored in file.");
		}
	}

	public boolean write(Entry entry) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					this.filename), true));
			bw.write("PDBID        : " + entry.getPDBID());
			bw.newLine();
			bw.write("Database     : " + entry.getDatabase().getName());
			bw.newLine();
			bw.write("Timestamp    : " + entry.getTimestamp());
			bw.newLine();
			bw.write("FilePath     : " + entry.getFilepath());
			bw.newLine();
			bw.write("//");
			bw.newLine();
			bw.flush();
			bw.close();
			return true;
		} catch (IOException e) {
			CommandlineWriter.cmd.printError("Writing " + this.filename
					+ " failed", "Crawled files not stored in file.");
			return false;
		}
	}
}
