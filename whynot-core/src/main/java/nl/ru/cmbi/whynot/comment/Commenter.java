package nl.ru.cmbi.whynot.comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;

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
			commentParser.comment(Converter.getFile(file));
		for (File file : dirUncomments.listFiles(CommentParser.commentFilter))
			commentParser.uncomment(Converter.getFile(file));

		//Cleanup unused comments & entries
		((CommentDAO) SpringUtil.getContext().getBean("commentDAO")).cleanUp();
		((EntryDAO) SpringUtil.getContext().getBean("entryDAO")).cleanUp();
	}
}
