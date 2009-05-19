package nl.ru.cmbi.whynot.hibernate;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import nl.ru.cmbi.whynot.comment.PhasedCommentParser;

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
		File dest = new File("comment/converted_20090519.txt");
		cp.storeComments(dest);
		cp.storeEntries(dest);
		//cp.storeAnnotations(dest);
	}
}
