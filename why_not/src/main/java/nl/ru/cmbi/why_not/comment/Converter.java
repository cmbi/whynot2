package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
	private Pattern	patternPDBID	= Pattern.compile("PDBID        : (.+)");
	private Pattern	patternDatabase	= Pattern.compile("Database     : (.+)");
	private Pattern	patternProperty	= Pattern.compile("Property     : (.+)");
	private Pattern	patternComment	= Pattern.compile("Comment      : (.+)");

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1)
			throw new IllegalArgumentException("Usage: converter FILENAME");
		new Converter().convert(new File(args[0]));
	}

	public File convert(File file) throws IOException, ParseException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		File convert = new File(file.getParent() + "/converted_" + file.getName());
		PrintWriter fw = new PrintWriter(new FileWriter(convert));

		String line, db, id, com = null;
		Matcher m;

		while ((line = lnr.readLine()) != null) {
			if (!(m = patternPDBID.matcher(line)).matches())
				throw new ParseException("Expected " + patternPDBID.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			id = m.group(1);

			if (!(m = patternDatabase.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternDatabase.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			db = m.group(1);

			if (!(m = patternProperty.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternProperty.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			//We scrapped property

			if (!(m = patternComment.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternComment.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			if (!m.group(1).equals(com)) {
				com = m.group(1);
				fw.println("COMMENT: " + com);
			}

			if (!lnr.readLine().matches("//"))
				throw new ParseException("Expected \"//\" on line " + lnr.getLineNumber(), lnr.getLineNumber());

			fw.println(db + "," + id);
		}
		lnr.close();
		fw.close();

		file.delete();
		return convert;
	}

}
