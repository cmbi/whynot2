package nl.ru.cmbi.whynot.hibernate;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import nl.ru.cmbi.whynot.comment.CommentParser;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;

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
	private CommentParser	cp;

	@Test
	@Ignore
	public void storeCommentsAndAnnotations() throws IOException, ParseException {
		File dest = new File("comment/20090519.txt.converted.optimized");
		cp.comment(dest);
	}

	@Test
	@Ignore
	public void removeAnnotations() throws IOException, ParseException {
		File dest = new File("uncomment/20090519.txt");
		cp.uncomment(dest);
	}

	@Autowired
	private CommentDAO	comdao;

	@Test
	@Ignore
	public void cleanUpComments() {
		comdao.cleanUp();
	}

	@Autowired
	private EntryDAO	entdao;

	@Test
	@Ignore
	public void cleanUpEntries() {
		entdao.cleanUp();
	}
}
