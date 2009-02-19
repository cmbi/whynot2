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
		this.printLine("Usage: whynot-crawler [OPTION]...");
		this
				.printLine("Collects, Stores and Validates information about Molecular Structure Data Files");
		this.printLine("in a Relational Database.");
		this.printLine("");
		this
				.printLine("Mandatory arguments to long options are mandatory for short options too.");
		this
				.printLine("  --help                       display this help and exit");
		this
				.printLine("  --version                    output version information and exit");
		this.printLine("");
		this
				.printLine("  -t, --type REGEX             regular expression describing the databases");
		this
				.printLine("                               (defined in databases.xml) to index");
		this
				.printLine("                               (default is .* (all databases consecutively))");
		this.printLine("");
		this
				.printLine("  -s, --source database /      define the information source for the entries.");
		this
				.printLine("               directory /     information source can be either the RDB,");
		this
				.printLine("               pdbfinderfile / a directory, a pdbfinderfile or none.");
		this.printLine("               none            (default is directory)");
		this.printLine("");
		this
				.printLine("  -p, --path DIRECTORY /       path to the directory containing the entries, OR");
		this
				.printLine("             FILE              path to the file containing entries");
		this
				.printLine("                               (default is . (local directory))");
		this.printLine("");
		this
				.printLine("  -nofilestorage               do not store entries in textfiles.");
		this
				.printLine("  -nodbstorage                 do not store entries in relational database.");
		this.printLine("");
		this
				.printLine("  -nodbvalidation              do not validate entries in relational database.");
		this.printLine("");
		this.printLine("Report bugs to <timtebeek@gmail.com>.");
		this.exit();
	}

	public void printVersion() {
		this.printLine("WhyNot - Crawler v 1.0");
		this.printLine("Written by Tim te Beek <timtebeek@gmail.com>");
		this
				.printLine("Centre for Molecular and Biomolecular Informatics, Nijmegen, the Netherlands");
		this.exit();
	}

	public void printHeader(String path, String source, String typeRegex,
			boolean storeFile, boolean storeDB, boolean validateDB) {
		this.printLine("--- Starting new run " + this.getDateTimeStamp());
		this.printLine("");
		this
				.printLine("### WhyNot - Crawler v1.0 ######################################################");
		this.printLine("");
		this.printLine("- Regex for database(s):        " + typeRegex);
		this.printLine("- Information Source:           " + source);
		this.printLine("- Path to directory or file:    " + path);
		this.printLine("- Store entries to file:        " + storeFile);
		this.printLine("- Store entries to database:    " + storeDB);
		this.printLine("- Validate entries in database: " + validateDB);
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
		String date = df.format(c.get(Calendar.MONTH) + 1) + "/"
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
