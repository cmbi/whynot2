package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.util.SpringUtil;

public class Annotater {
	public static void main(String[] args) throws IOException, ParseException {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Any files not already done
		FileFilter commentFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().contains(CommentParser.append);
			}
		};

		CommentParser commentParser = (CommentParser) SpringUtil.getContext().getBean("commentParser");
		//Comment / Uncomment all files in directories
		for (File file : dirComments.listFiles(commentFilter))
			commentParser.comment(Converter.getFile(file));
		for (File file : dirUncomments.listFiles(commentFilter))
			commentParser.uncomment(Converter.getFile(file));

		EntryDAO entdao = (EntryDAO) SpringUtil.getContext().getBean("entryDAO");
		entdao.removeEntriesWithoutBothFileAndParentFile();
	}
}
