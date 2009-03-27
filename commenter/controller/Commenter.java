package controller;

import inout.CommandlineWriter;
import inout.Progress;
import inout.ProgressWriter;
import inout.database.JDBCXMLReader;
import inout.file.CommentsBackupWriter;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import model.Comment;
import model.Database;
import model.PDBID;
import model.Property;
import persistance.IPersistance;
import persistance.RDB;

public class Commenter {
	private boolean backup = false;
	private boolean comment = false;
	private boolean uncomment = false;

	private String cpath = "./comments/";
	private String upath = "./uncomments/";

	public Commenter(String[] args) {
		this.parseArguments(args);

		CommandlineWriter.cmd.printHeader(this.backup, this.comment, this.cpath, this.uncomment, this.upath);

		if (this.backup)
			this.backupComments();

		if (this.comment) {
			File pathfile = new File(this.cpath);
			if (pathfile.isDirectory())
				for (File textfile : pathfile.listFiles(new TextFileFilter()))
					this.parseComments(textfile);
			else if (new TextFileFilter().accept(pathfile))
				this.parseComments(pathfile);
		}

		if (this.uncomment) {
			File pathfile = new File(this.upath);
			if (pathfile.isDirectory())
				for (File textfile : pathfile.listFiles(new TextFileFilter()))
					this.parseUncomments(textfile);
			else if (new TextFileFilter().accept(pathfile))
				this.parseUncomments(pathfile);
		}

		CommandlineWriter.cmd.printProgress(
				"Caching reports",
				"This may take a while...");
		new RDB(new JDBCXMLReader().getProperties()).cleanUp();

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

			if (arg.equals("-b") || arg.equals("--backup")) {
				this.backup = true;
				continue;
			}

			if (arg.equals("-c") || arg.equals("--comment")) {
				if (c < args.length - 1) {
					this.cpath = args[c + 1];
					if (!new File(this.cpath).exists())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -c / --comment does not exist",
										"Check parameter and ensure proper access rights.");
					if (!new File(this.cpath).canRead())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -c / --comment could not be read",
										"Check parameter and ensure proper access rights.");
				} else
					CommandlineWriter.cmd.printFatalError(
							"Mandatory parameter for argument -c / --comment missing",
							"Read --help for help in using arguments.");
				c++;
				this.comment = true;
				continue;
			}

			if (arg.equals("-u") || arg.equals("--uncomment")) {
				if (c < args.length - 1) {
					this.upath = args[c + 1];
					if (!new File(this.upath).exists())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -u / --uncomment does not exist",
										"Check parameter and ensure proper access rights.");
					if (!new File(this.upath).canRead())
						CommandlineWriter.cmd
								.printFatalError(
										"Parameter for argument -u / --uncomment could not be read",
										"Check parameter and ensure proper access rights.");
				} else
					CommandlineWriter.cmd.printFatalError(
							"Mandatory parameter for argument -u / --uncomment missing",
							"Read --help for help in using arguments.");
				c++;
				this.uncomment = true;
				continue;
			}

			CommandlineWriter.cmd
					.printFatalError("Unrecognized argument '" + args[c] + "'",
							"Read --help for help in using arguments.");
		}
	}

	private void backupComments() {
		IPersistance db = new RDB(new JDBCXMLReader().getProperties());

		CommandlineWriter.cmd.printProgress("### backup", "###");

		Progress progress = new Progress("Backuping comments");
		ProgressWriter pgwrtr = new ProgressWriter(progress);

		String[][] commentrecords = db.getCommentRecords();
		CommentsBackupWriter cbw = new CommentsBackupWriter();
		for (String[] record : commentrecords)
			if (cbw.write(record))
				progress.increaseNoSucces();
			else
				progress.increaseNoFailed();

		pgwrtr.stop();

		CommandlineWriter.cmd.printLine();
	}

	private void parseComments(File textfile) {
		IPersistance db = new RDB(new JDBCXMLReader().getProperties());

		CommandlineWriter.cmd.printProgress("### " + textfile.getName(), "###");

		Progress progress = new Progress("Adding comments");
		ProgressWriter pgwrtr = new ProgressWriter(progress);

		try {
			LineNumberReader lnr = new LineNumberReader(
					new FileReader(textfile));

			String[] values;
			while (true) {
				values = this.readCommentBlock(lnr, progress, textfile.getName());
				if (values == null) // Error reading block
					continue;
				if (values.length == 0) // Proper EOF
					break;

				PDBID pdbid = db.getPDBID(values[0].toLowerCase());
				if (pdbid == null) {
					CommandlineWriter.cmd.printError("PDBID \"" + values[0]
							+ "\" is not stored in the database",
							"Comment ignored");
					progress.increaseNoFailed();
					continue;
				}
				Database database = db.getDatabase(values[1]);
				if (database == null) {
					CommandlineWriter.cmd.printError("Database \"" + values[1]
							+ "\" is not stored in the database",
							"Comment ignored");
					progress.increaseNoFailed();
					continue;
				}
				Property property = db.getProperty(values[2]);
				if (property == null)
					property = new Property(values[2], "");

				if (values.length == 4) {
					Comment comment = db.getComment(values[3]);
					if (comment == null)
						comment = new Comment(values[3]);

					if (db.addCommentForPropertyOfEntry(pdbid, database,
							property, comment))
						progress.increaseNoSucces();
					else
						progress.increaseNoFailed();
				}

				if (values.length == 5) {
					boolean bool = values[3].equalsIgnoreCase("true");
					Comment comment = db.getComment(values[4]);
					if (comment == null)
						comment = new Comment(values[4]);

					if (db.addCommentForPropertyOfEntry(pdbid, database,
							property, bool, comment))
						progress.increaseNoSucces();
					else
						progress.increaseNoFailed();
				}
			}
		} catch (FileNotFoundException e) {
			CommandlineWriter.cmd.printError(textfile.getName()
					+ " could not be read", "Reading file "
					+ textfile.getName() + " failed");
		} catch (IOException e) {
			CommandlineWriter.cmd.printError(textfile.getName()
					+ " could not be read", "Reading file "
					+ textfile.getName() + " failed");
		}

		pgwrtr.stop();
		textfile.renameTo(new File(textfile.getPath() + "_done"));

		CommandlineWriter.cmd.printLine();
	}

	private void parseUncomments(File textfile) {
		IPersistance db = new RDB(new JDBCXMLReader().getProperties());

		CommandlineWriter.cmd.printProgress("### " + textfile.getName(), "###");

		Progress progress = new Progress("Removing comments");
		ProgressWriter pgwrtr = new ProgressWriter(progress);

		try {
			LineNumberReader lnr = new LineNumberReader(
					new FileReader(textfile));

			String[] values;
			while (true) {
				values = this.readCommentBlock(lnr, progress, textfile.getName());
				if (values == null) // Error reading block
					continue;
				if (values.length == 0) // Proper EOF
					break;

				PDBID pdbid = db.getPDBID(values[0].toLowerCase());
				if (pdbid == null) {
					CommandlineWriter.cmd.printError("PDBID \"" + values[0]
							+ "\" is not stored in the database",
							"Comment ignored");
					progress.increaseNoFailed();
					continue;
				}
				Database database = db.getDatabase(values[1]);
				if (database == null) {
					CommandlineWriter.cmd.printError("Database \"" + values[1]
							+ "\" is not stored in the database",
							"Comment ignored");
					progress.increaseNoFailed();
					continue;
				}
				Property property = db.getProperty(values[2]);
				if (property == null)
					property = new Property(values[2], "");

				if (values.length == 4) {
					Comment comment = db.getComment(values[3]);
					if (comment == null)
						comment = new Comment(values[3]);

					if (db.removeCommentForPropertyOfEntry(pdbid, database,
							property, comment))
						progress.increaseNoSucces();
					else
						progress.increaseNoFailed();
				}

				if (values.length == 5) {
					//boolean bool = values[3].equalsIgnoreCase("true");
					Comment comment = db.getComment(values[4]);
					if (comment == null)
						comment = new Comment(values[4]);

					if (db.removeCommentForPropertyOfEntry(pdbid, database,
							property, comment))
						progress.increaseNoSucces();
					else
						progress.increaseNoFailed();
				}
			}
		} catch (FileNotFoundException e) {
			CommandlineWriter.cmd.printError(textfile.getName()
					+ " could not be read", "Reading file "
					+ textfile.getName() + " failed");
		} catch (IOException e) {
			CommandlineWriter.cmd.printError(textfile.getName()
					+ " could not be read", "Reading file "
					+ textfile.getName() + " failed");
		}

		pgwrtr.stop();
		textfile.renameTo(new File(textfile.getPath() + "_done"));

		CommandlineWriter.cmd.printLine();
	}

	private String[] readCommentBlock(LineNumberReader lnr, Progress progress, String filename)
			throws EOFException {
		String sPDBID, sDatabase, sProperty, sBoolean = null, sComment = null;

		String line;
		try {
			if ((line = lnr.readLine()) == null)
				return new String[]{};
			if (!line.matches("PDBID        : [\\d\\w]{4}"))
				throw new IOException("Malformed line");
			sPDBID = line.substring(15).trim();

			if ((line = lnr.readLine()) == null)
				throw new EOFException();
			if (!line.matches("Database     : .*"))
				throw new IOException("Malformed line");
			sDatabase = line.substring(15).trim();

			if ((line = lnr.readLine()) == null)
				throw new EOFException();
			if (!line.matches("Property     : .*"))
				throw new IOException("Malformed line");
			sProperty = line.substring(15).trim();

			if ((line = lnr.readLine()) == null)
				throw new EOFException();
			else {
				if (!line.matches("Boolean      : (false|true)")) { // The
					// booleanfield
					// is
					// optional
					if (!line.matches("Comment      : .*"))
						throw new IOException("Malformed line");
					else
						sComment = line.substring(15).trim();
				} else
					sBoolean = line.substring(15).trim();
			}

			if ((line = lnr.readLine()) == null)
				throw new EOFException();
			else {
				if (!line.matches("Comment      : .*")) { // If we already
					// read the comment
					if (!line.matches("//"))
						throw new IOException("Malformed line");
					else
						return new String[] { sPDBID, sDatabase, sProperty,
								sComment };
				} else
					sComment = line.substring(15).trim();
			}

			if ((line = lnr.readLine()) == null || !line.matches("//"))
				throw new IOException("Malformed line");

			return new String[] { sPDBID, sDatabase, sProperty, sBoolean,
					sComment };
		} catch (EOFException e) {
			CommandlineWriter.cmd.printError("EOF reached prematurely in "
					+ filename + " at line number " + lnr.getLineNumber(),
					"Comment ignored");
			try {
				lnr.close();
			} catch (IOException e1) {
			}
			throw new EOFException();
		} catch (IOException e) {
			CommandlineWriter.cmd.printError("Malformed line encountered in "
					+ filename + " at line number " + lnr.getLineNumber(),
					"Comment ignored");
			try {
				while((line = lnr.readLine()) != null && !line.matches("//")); // Skip till end of block
				progress.increaseNoFailed();
			} catch (IOException e1) {}
			return null;
		}
	}
}
