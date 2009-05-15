package nl.ru.cmbi.why_not.hibernate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;

import nl.ru.cmbi.why_not.comment.PhasedCommentParser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class CommentParserTest {
	@Autowired
	private PhasedCommentParser	cp;

	@Test
	public void copyFileToCommentAndComment() throws IOException, ParseException {
		File dest = new File("comment/testcomments.txt");
		PrintWriter pw = new PrintWriter(new FileWriter(dest));
		//File src = new File("src/test/resources/testcomments.txt");
		File src = new File("src/test/resources/converted_20090407_comments.txt");
		LineNumberReader lnr = new LineNumberReader(new FileReader(src));

		String line;
		while ((line = lnr.readLine()) != null)
			pw.println(line);
		lnr.close();
		pw.close();

		cp.storeComments(dest);
		cp.storeEntries(dest);
	}

	@Test
	@Ignore
	public void copyFileToUncommentAndUncomment() throws IOException, ParseException {
		File dest = new File("uncomment/testcomments.txt");
		PrintWriter pw = new PrintWriter(new FileWriter(dest));
		File src = new File("src/test/resources/testcomments.txt");
		LineNumberReader lnr = new LineNumberReader(new FileReader(src));

		String line;
		while ((line = lnr.readLine()) != null)
			pw.println(line);
		lnr.close();
		pw.close();

		;//cp.uncomment(dest);
	}
}
