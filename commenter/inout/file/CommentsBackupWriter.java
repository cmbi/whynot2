package inout.file;

import inout.CommandlineWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class CommentsBackupWriter {
	private String filename = "comments.txt";

	public CommentsBackupWriter() {
		String prefix = getDatestamp();
		File directory = new File("./backups/");
		if (directory.exists() || directory.mkdir())
			prefix = "./backups/" + prefix;
		this.filename = prefix + "_" + this.filename;
		try {
			File f = new File(this.filename);
			FileWriter fw = new FileWriter(f);
			new BufferedWriter(fw).close(); // Clear file contents
		} catch (IOException e) {
			CommandlineWriter.cmd.printFatalError("Writing " + this.filename
					+ " failed", "Comments backup not stored in file.");
		}
	}

	private String getDatestamp() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + df.format(c.get(Calendar.MONTH)+1) + df.format(c.get(Calendar.DATE));
	}

	public boolean write(String[] record) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.filename), true));
			bw.write("PDBID        : " + record[1]);
			bw.newLine();
			bw.write("Database     : " + record[0]);
			bw.newLine();
			bw.write("Property     : " + record[2]);
			bw.newLine();
			bw.write("Comment      : " + record[3]);
			bw.newLine();
			bw.write("//");
			bw.newLine();
			bw.flush();
			bw.close();
			return true;
		} catch (IOException e) {
			CommandlineWriter.cmd.printError("Writing " + this.filename
					+ " failed", "Comments backup not stored in file.");
			return false;
		}
	}
}
