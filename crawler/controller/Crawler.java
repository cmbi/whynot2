package controller;

import inout.CommandlineWriter;
import inout.Progress;
import inout.ProgressWriter;
import inout.crawl.DirectoryCrawler;
import inout.database.JDBCXMLReader;
import inout.database.RDBReader;
import inout.database.RDBValidator;
import inout.database.RDBWriter;
import inout.file.CrawledFilesWriter;
import inout.file.DatabasesXMLReader;
import inout.file.PDBFinderFileReader;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import model.Database;
import model.Entry;
import persistance.RDB;

/**
 * This class parses the commandline arguments and determines which operations
 * need to be preformed. It then executes the given operations for each of the
 * enabled file types PDB and DSSP.
 *
 * @author tbeek
 *
 */
public class Crawler {
	private enum InformationSource {
		DIRECTORY("directory"), DATABASE("database"), PDBFINDERFILE(
				"pdbfinderfile"), NONE("none");

		private final String parameter;

		private InformationSource(String parameter) {
			this.parameter = parameter;
		}

		private boolean matchesArgument(String c1) {
			return this.parameter.equals(c1);
		}
	};

	// Default path is local directory
	private String path = ".";

	private InformationSource source = InformationSource.DIRECTORY;

	private String typeRegex = ".*";

	private Database[] databases = new DatabasesXMLReader().getDatabases();

	private boolean storeFile = true;

	private boolean storeDB = true;

	private boolean validateDB = true;

	public Crawler(String[] args) {
		this.parseArguments(args);

		CommandlineWriter.cmd.printHeader(this.path, this.source.parameter,
				this.typeRegex, this.storeFile, this.storeDB, this.validateDB);

		for (Database database : this.databases)
			if (database.getName().matches(this.typeRegex)) {
				CommandlineWriter.cmd.printProgress(
						"### " + database.getName(), "###");

				CrawledFilesWriter cfw = new CrawledFilesWriter(database
						.getName());
				RDBWriter dbw = new RDBWriter();

				Progress progress = new Progress("Processing files");
				ProgressWriter pgwrtr = new ProgressWriter(progress);

				Iterator<Entry> entryIterator = this.getEntryIterator(
						this.source, this.path, database);
				while (entryIterator.hasNext()) {
					boolean success = true;
					Entry msdfile = entryIterator.next();

					if (this.storeFile && !cfw.write(msdfile))
						success = false;
					if (this.storeDB && !dbw.write(msdfile))
						success = false;

					if (success)
						progress.increaseNoSucces();
					else
						progress.increaseNoFailed();
				}
				entryIterator = null;

				pgwrtr.stop();

				if (this.validateDB)
					new RDBValidator(database);

				if (this.storeDB || this.validateDB) {
					CommandlineWriter.cmd.printProgress("Caching reports", "This may take a while...");
					new RDB(new JDBCXMLReader().getProperties()).cleanUp(database.getName());
				}

				CommandlineWriter.cmd.printLine();
			}

		CommandlineWriter.cmd.exit();
	}

	private void parseArguments(String[] args) {
		if (args.length == 0)
			CommandlineWriter.cmd.printHelp();
		for (int c = 0; c < args.length; c++) {
			String arg = args[c];

			if (arg.equals("--help")) {
				CommandlineWriter.cmd.printHelp();
				continue;
			}

			if (arg.equals("--version")) {
				CommandlineWriter.cmd.printVersion();
				continue;
			}

			if (arg.equals("-t") || arg.equals("--type")) {
				if (c < args.length - 1)
					this.typeRegex = args[c + 1];
				else
					CommandlineWriter.cmd.printFatalError(
							"Mandatory type missing for argument -t / --type",
							"Read --help for help in using arguments.");
				c++;
				continue;
			}

			if (arg.equals("-s") || arg.equals("--source")) {
				if (c < args.length - 1) {
					String arg2 = args[c + 1];
					boolean matched = false;
					for (InformationSource s : InformationSource.values())
						if (s.matchesArgument(arg2)) {
							this.source = s;
							matched = true;
						}
					if (!matched)
						CommandlineWriter.cmd
								.printFatalError(
										"Malformed parameter for argument -s / --source",
										"Read --help for help in using arguments.");
				} else
					CommandlineWriter.cmd
							.printFatalError(
									"Mandatory source missing for argument -s / --source",
									"Read --help for help in using arguments.");
				c++;
				continue;
			}

			if (arg.equals("-p") || arg.equals("--path")) {
				if (c < args.length - 1) {
					this.path = args[c + 1];
					if (!new File(this.path).exists())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -p / --path does not exist",
										"Check parameter and ensure proper access rights.");
					if (!new File(this.path).canRead())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -p / --path could not be read",
										"Check parameter and ensure proper access rights.");
				} else
					CommandlineWriter.cmd.printFatalError(
							"Mandatory path missing for argument -p / --path",
							"Read --help for help in using arguments.");
				c++;
				continue;
			}

			if (arg.equals("-nofilestorage")) {
				this.storeFile = false;
				continue;
			}
			if (arg.equals("-nodbstorage")) {
				this.storeDB = false;
				continue;
			}
			if (arg.equals("-nodbvalidation")) {
				this.validateDB = false;
				continue;
			}

			CommandlineWriter.cmd.printFatalError("Unrecognized argument '" + args[c]
					+ "'", "Read --help for help in using arguments.");
		}
	}

	private Iterator<Entry> getEntryIterator(InformationSource source,
			String path, Database database) {
		switch (source) {
		case DATABASE:
			return new RDBReader(database);
		case DIRECTORY:
			if (!database.getName().equals("PDBFINDER"))
				return new DirectoryCrawler(path, database);
			break;
		case PDBFINDERFILE:
			if (database.getName().equals("PDBFINDER"))
				return new PDBFinderFileReader(path, database);
			break;
		}
		return new Vector<Entry>().iterator();
	}
}
