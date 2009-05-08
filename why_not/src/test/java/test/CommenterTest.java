package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import org.junit.AfterClass;
import org.junit.Test;

import comment.Commenter;

public class CommenterTest {
	private static FilenameFilter	doneFilter	= new FilenameFilter() {
													public boolean accept(File dir, String name) {
														return !name.contains(append);
													}
												};
	private static String			append		= ".done";

	@Test
	public void comment() throws Exception {
		new Commenter();
	}

	@AfterClass
	public static void resetfiles() throws Exception {
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
