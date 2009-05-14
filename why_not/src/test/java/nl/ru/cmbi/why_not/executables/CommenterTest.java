package nl.ru.cmbi.why_not.executables;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import nl.ru.cmbi.why_not.comment.Commenter;

import org.junit.After;
import org.junit.Test;

public class CommenterTest {
	private FileFilter	doneFilter	= new FileFilter() {
										public boolean accept(File pathname) {
											return pathname.isFile() && pathname.getName().contains(append);
										}
									};
	private String		append		= ".done";

	@Test
	public void comment() throws Exception {
		Commenter.main(new String[0]);
	}

	@After
	public void resetfiles() throws Exception {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Rename files
		for (File file : dirComments.listFiles(doneFilter))
			file.renameTo(new File(file.getAbsolutePath().replace(append, "")));
		for (File file : dirUncomments.listFiles(doneFilter))
			file.renameTo(new File(file.getAbsolutePath().replace(append, "")));
	}
}
