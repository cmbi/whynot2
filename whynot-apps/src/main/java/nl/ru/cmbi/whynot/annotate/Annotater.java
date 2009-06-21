package nl.ru.cmbi.whynot.annotate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import nl.ru.cmbi.whynot.util.SpringUtil;

import org.apache.log4j.Logger;

public class Annotater {

	public static void main(String[] args) throws Exception {
		Logger.getLogger(Annotater.class).info("Annotater start.");
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

		commentParser.removeEntriesWithoutBothFileAndParentFile();
		Logger.getLogger(Annotater.class).info("Annotater done.");
	}
}
