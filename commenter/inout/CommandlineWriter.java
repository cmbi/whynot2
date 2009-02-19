package inout;

import java.text.DecimalFormat;
import java.util.Calendar;

import persistance.PersistanceException;

/**
 * This class provides a uniform way of writing various sorts of output to the
 * commandline, and is accessed throughout the code via the public static
 * CommandlineWriter.cmd.
 *
 * @author tbeek
 *
 */
public class CommandlineWriter {
	public static final CommandlineWriter cmd = new CommandlineWriter();

	private boolean errorOccurred = false;

	private CommandlineWriter() {
	}

	public void printHelp() {
		this.printLine("Usage: whynot-commenter [OPTION]...");
		this.printLine("Stores comments about properties of Molecular Structure Data Files in a");
		this.printLine("Relational Database.");
		this.printLine();
		this.printLine("Mandatory arguments to long options are mandatory for short options too.");
		this.printLine("  --help                       display this help and exit");
		this.printLine("  --version                    output version information and exit");
		this.printLine();
		this.printLine("  -b, --backup                 write a file backup of all the currently stored");
		this.printLine("                               comments to the ./comments/ directory");
		this.printLine();
		this.printLine("  -c, --comment DIRECTORY /    path to the directory containing textfiles, OR");
		this.printLine("                FILE           path to the textfile containing comments");
		this.printLine("                               (default is: ./comments/ (all textfiles))");
		this.printLine("                               textfiles should have the following format:");
		this.printLine();
		this.printLine("                               PDBID        : 1XJ9");
		this.printLine("                               Database     : DSSP");
		this.printLine("                               Property     : Exists");
		this.printLine("                               Boolean      : true/false (line optional)");
		this.printLine("                               Comment      : Not enough amino acids");
		this.printLine("                               //");
		this.printLine();
		this.printLine("  -u, --uncomment DIRECTORY /  path to the directory containing textfiles, OR");
		this.printLine("                FILE           path to the textfile containing uncomments");
		this.printLine("                               (default is: ./uncomments/ (all textfiles))");
		this.printLine("                               textfiles should have the following format:");
		this.printLine();
		this.printLine("                               PDBID        : 1XJ9");
		this.printLine("                               Database     : DSSP");
		this.printLine("                               Property     : Exists");
		this.printLine("                               Comment      : Not enough amino acids");
		this.printLine("                               //");
		this.printLine();
		this.printLine("Report bugs to <timtebeek@gmail.com>.");
		this.exit();
	}

	public void printVersion() {
		this.printLine("WhyNot - Batch Commenter v 1.1");
		this.printLine("Written by Tim te Beek <timtebeek@gmail.com>");
		this.printLine("Centre for Molecular and Biomolecular Informatics, Nijmegen, the Netherlands");
		this.exit();
	}

	public void printHeader(boolean backup, boolean comment, String cpath, boolean uncomment, String upath) {
		this.printLine("--- Starting new run " + this.getDateTimeStamp());
		this.printLine("");
		this.printLine("### WhyNot - Batch Commenter v1.1 ##############################################");
		this.printLine("");
		this.printLine("- Backup comments to file: " + backup);
		this.printLine("- Add comments in "+cpath+": " + comment);
		this.printLine("- Remove comments in "+upath+": " + uncomment);
		this.printLine("");
	}

	public void printLine() {
		this.printLine("");
	}

	private void printLine(String line) {
		System.out.println(line);
	}

	public void printProgress(String stage, String result) {
		this.printLine(this.getTimeStamp() + " " + stage + ": " + result);
	}

	public void printFatalError(String error, String solution) {
		this.printError(error, solution);
		this.exit();
	}

	public void printPersistanceError(String action, PersistanceException e) {
		// You cant really fix an SQL error, but its not always necessary to
		// halt execution over it.
		// Log it with details for the programmer and proceed with normal
		// execution.
		this.printError("Persistance Error in '" + action + "'", e.getMessage()
				+ " - " + e.toString());
	}

	public void printError(String error, String solution) {
		System.err.println(this.getTimeStamp() + " Error: " + error + ": "
				+ solution);
		this.errorOccurred = true;
	}

	private String getTimeStamp() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar c = Calendar.getInstance();
		String time = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ df.format(c.get(Calendar.MINUTE)) + ":"
				+ df.format(c.get(Calendar.SECOND));
		String timestamp = "[" + time + "]";
		return timestamp;
	}

	private String getDateTimeStamp() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar c = Calendar.getInstance();
		String date = df.format(c.get(Calendar.MONTH)+1) + "/"
				+ df.format(c.get(Calendar.DATE)) + "/" + c.get(Calendar.YEAR);
		String time = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ df.format(c.get(Calendar.MINUTE)) + ":"
				+ df.format(c.get(Calendar.SECOND));
		String timestamp = "[" + date + ", " + time + "]";
		return timestamp;
	}

	public void exit() {
		int exitStatus = 0;
		if (this.errorOccurred) {
			this
					.printLine(this.getTimeStamp()
							+ " Some errors occurred: See stdout and stderr for details.");
			exitStatus = 1;
		}
		this.printLine(this.getTimeStamp() + " Execution completed.");
		System.exit(exitStatus);
	}
}
