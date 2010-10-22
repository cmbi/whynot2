package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter {
	private static final Logger	log	= LoggerFactory.getLogger(Converter.class);

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1)
			throw new IllegalArgumentException("Usage: converter FILENAME");
		convert(new File(args[0]));
	}

	//Old
	private static final Pattern	patternPDBID	= Pattern.compile("PDBID        : (.+)");
	private static final Pattern	patternDatabase	= Pattern.compile("Database     : (.+)");
	private static final Pattern	patternProperty	= Pattern.compile("Property     : (.+)");
	private static final Pattern	patternComment	= Pattern.compile("Comment      : (.+)");

	//New
	public static final Pattern		patternCOMMENT	= Pattern.compile("COMMENT: (.+)");
	public static final Pattern		patternEntry	= Pattern.compile("(.+),([a-zA-Z0-9]{4})");

	/**
	 * Try to read the file, and if necessary convert & optimize it.
	 * 
	 * @param file
	 * @return the optionally converted and/or optimized file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static File getFile(File file) throws FileNotFoundException, IOException, ParseException {
		Scanner scnr = new Scanner(file);
		if (!scnr.hasNextLine())
			throw new ParseException("Comment files can not be empty: Must contain at least one line", 1);
		String line = scnr.nextLine();
		scnr.close();

		if (line.startsWith("PDBID"))
			return convert(file);
		if (line.startsWith("COMMENT"))
			return optimize(file);
		throw new ParseException("Could not determine Comment file type: Expected PDBID or COMMENT on line 1", 1);
	}

	/**
	 * Converts an old style comment file into new style comment file,
	 * only writing a new comment line if different from previous line.
	 * Old file is replaced with new file named OldFileName.converted.
	 * 
	 * @param original
	 * @return converted file
	 * @throws IOException
	 * @throws ParseException
	 */
	public static File convert(File original) throws IOException, ParseException {
		log.info("Converting file " + original.getAbsolutePath());

		LineNumberReader lnr = new LineNumberReader(new FileReader(original));
		File converted = new File(original.getAbsolutePath() + ".converted");
		PrintWriter fw = new PrintWriter(new FileWriter(converted));

		String line, prev_com = null, com, db, id;
		Matcher m;

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
			//Ignore Property

			//Comment
			if (!(m = patternComment.matcher(lnr.readLine())).matches())
				throw new ParseException("Expected " + patternComment.pattern() + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
			com = m.group(1);
			if (!com.equals(prev_com))
				fw.println("COMMENT: " + (prev_com = com));

			//
			if (!lnr.readLine().matches("//"))
				throw new ParseException("Expected \"//\" on line " + lnr.getLineNumber(), lnr.getLineNumber());

			//Add new entry line
			fw.println(db + "," + id);
		}
		lnr.close();
		fw.close();

		original.delete();
		return optimize(converted);
	}

	/**
	 * Optimizes new style comment file by removing duplicate comment
	 * lines, sorting entry lines beneath each comment, and ordering
	 * the comments from small to large.
	 * 
	 * @param original
	 * @return optimized file
	 * @throws IOException
	 * @throws ParseException
	 */
	public static File optimize(File original) throws IOException, ParseException {
		log.info("Optimizing file " + original.getAbsolutePath());

		LineNumberReader lnr = new LineNumberReader(new FileReader(original));
		File optimized = new File(original.getAbsolutePath() + ".optimized");

		SortedMap<String, SortedSet<String>> mapje = new TreeMap<String, SortedSet<String>>();

		//Read
		String line, com = "COMMENT: Empty comment";
		while ((line = lnr.readLine()) != null) {
			line = line.trim();
			//Entry 
			if (patternEntry.matcher(line).matches())
				//Add new entry line
				mapje.get(com).add(line);
			else
				//Comment
				if (patternCOMMENT.matcher(line).matches()) {
					if (!mapje.containsKey(com = line))
						mapje.put(line, new TreeSet<String>());
				}
				else
					throw new ParseException("Expected " + patternCOMMENT + " or " + patternEntry + " on line " + lnr.getLineNumber(), lnr.getLineNumber());
		}
		lnr.close();

		//Write
		PrintWriter fw = new PrintWriter(new FileWriter(optimized));
		while (!mapje.isEmpty()) {
			String smallest = mapje.firstKey();
			int size = mapje.get(smallest).size();
			for (Entry<String, SortedSet<String>> ce : mapje.entrySet())
				if (ce.getValue().size() < size) {
					smallest = ce.getKey();
					size = ce.getValue().size();
				}

			//Print comment
			fw.println(smallest);
			for (String entryline : mapje.remove(smallest))
				fw.println(entryline);
		}
		fw.close();

		original.delete();
		return optimized;
	}
}
