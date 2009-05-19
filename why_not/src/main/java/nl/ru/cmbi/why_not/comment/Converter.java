package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
	private static Pattern	patternPDBID	= Pattern.compile("PDBID        : (.+)");
	private static Pattern	patternDatabase	= Pattern.compile("Database     : (.+)");
	private static Pattern	patternProperty	= Pattern.compile("Property     : (.+)");
	private static Pattern	patternComment	= Pattern.compile("Comment      : (.+)");

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1)
			throw new IllegalArgumentException("Usage: converter FILENAME");
		convert(new File(args[0]));
	}

	/**
	 * Converts an old style comment file into new style comment file,
	 * only writing a new comment line if different from previous line.
	 * Old file is replaced with new file named: converted_OldFileName.
	 * @param file
	 * @return converted file
	 * @throws IOException
	 * @throws ParseException
	 */
	public static File convert(File file) throws IOException, ParseException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		File convert = new File(file.getParent() + "/converted_" + file.getName());

		String line, com = null, db, id;
		Matcher m;

		SortedMap<String, SortedSet<String>> mapje = new TreeMap<String, SortedSet<String>>();

		while ((line = lnr.readLine()) != null) {
			//PDBID
			if (!(m = patternPDBID.matcher(line)).matches())
				throw new ParseException("Expected " + patternPDBID.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			id = m.group(1);

			//Database
			if (!(m = patternDatabase.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternDatabase.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			db = m.group(1);

			//Property
			if (!(m = patternProperty.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternProperty.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			;//Ignore Property

			//Comment
			if (!(m = patternComment.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternComment.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			com = m.group(1);
			if (!mapje.containsKey(com))
				mapje.put(com, new TreeSet<String>());

			//
			if (!lnr.readLine().matches("//"))
				throw new ParseException("Expected \"//\" on line " + lnr.getLineNumber(), lnr.getLineNumber());

			//Add new entry line
			mapje.get(com).add(db + "," + id);
		}
		lnr.close();

		PrintWriter fw = new PrintWriter(new FileWriter(convert));
		for (String comment : mapje.keySet()) {
			fw.println("COMMENT: " + comment);
			for (String entry : mapje.get(comment))
				fw.println(entry);
		}
		fw.close();

		file.delete();
		return convert;
	}
}
