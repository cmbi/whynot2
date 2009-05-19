package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;

import nl.ru.cmbi.why_not.hibernate.SpringUtil;

public class Commenter {
	public static void main(String[] args) throws IOException, ParseException {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		CommentParser commentParser = (CommentParser) SpringUtil.getContext().getBean("commentParser");
		//Comment / Uncomment all files in directories
		for (File file : dirComments.listFiles(CommentParser.commentFilter))
			commentParser.comment(getFile(file));
		for (File file : dirUncomments.listFiles(CommentParser.commentFilter))
			commentParser.uncomment(getFile(file));

		//Cleanup unused comments & entries
		commentParser.cleanUpComments();
		commentParser.cleanUpEntries();
	}

	/**
	 * Try to read the file, and if it's not in the right format convert it.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	private static File getFile(File file) throws FileNotFoundException, IOException, ParseException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line = lnr.readLine();
		lnr.close();

		if (line.startsWith("PDBID"))
			return Converter.convert(file);
		if (line.startsWith("COMMENT"))
			return file;
		throw new ParseException("Could not determine Comment file type: Expected PDBID or COMMENT on line 1", 1);
	}
}
